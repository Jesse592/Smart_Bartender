import time
import json
import threading
import traceback
import socket
import RPi.GPIO as GPIO

#HOST = "192.168.4.1"  # Make IP static or update field dynamicly
HOST = "145.49.57.118"
PORT = 65432

LEFT_BTN_PIN = 13
LEFT_PIN_BOUNCE = 1000

RIGHT_BTN_PIN = 5
RIGHT_PIN_BOUNCE = 2000

FLOW_RATE = 60.0/100.0

class Bartender(): 
	def __init__(self):
		self.running = False
  
		self.btn1Pin = LEFT_BTN_PIN
		self.btn2Pin = RIGHT_BTN_PIN
  
		# load the pump configuration from file
		self.pump_configuration = Bartender.readPumpConfiguration()
  
		for pump in self.pump_configuration.keys():
			GPIO.setup(self.pump_configuration[pump]["pin"], GPIO.OUT, initial=GPIO.HIGH)

		print("Done initializing")

	@staticmethod
	def readPumpConfiguration():
		return json.load(open('pump_config.json'))

	@staticmethod
	def writePumpConfiguration(configuration):
		with open("pump_config.json", "w") as jsonFile:
			json.dump(configuration, jsonFile)

	def startInterrupts(self):
		print("Button interupt started")
		GPIO.add_event_detect(self.btn1Pin, GPIO.FALLING, callback=self.left_btn, bouncetime=LEFT_PIN_BOUNCE)
		GPIO.add_event_detect(self.btn2Pin, GPIO.FALLING, callback=self.right_btn, bouncetime=RIGHT_PIN_BOUNCE)  

	def stopInterrupts(self):
		print("Button interupt stoped")
		GPIO.remove_event_detect(self.btn1Pin)
		GPIO.remove_event_detect(self.btn2Pin)

	def clean(self):
		waitTime = 20
		pumpThreads = []

		self.running = True

		for pump in self.pump_configuration.keys():
			pump_t = threading.Thread(target=self.pour, args=(self.pump_configuration[pump]["pin"], waitTime))
			pumpThreads.append(pump_t)

		for thread in pumpThreads:
			thread.start()

		self.progressBar(waitTime)

		# wait for threads to finish
		for thread in pumpThreads:
			thread.join()

		# sleep for a couple seconds to make sure the interrupts don't get triggered
		time.sleep(2)

		self.updateIsActive(False)

	def updateIsActive(self, conn: socket, running, progress = None):
		self.running = running

		conn.send((json.dumps({'command': 'RecipeChanged', 'data': {'isProcessing': running, 'progress': progress, 'recipe': self.recipe}}) + "\r\n").encode())

	def pour(self, pin, waitTime):
		GPIO.output(pin, GPIO.LOW)
		time.sleep(waitTime)
		GPIO.output(pin, GPIO.HIGH)

	def progressBar(self, conn, waitTime):
		interval = waitTime / 100.0
		for x in range(1, 101):
			# Update LED screen
			time.sleep(interval)
			self.updateIsActive(conn, True, x)

	def makeDrink(self, conn: socket, recipe):
		self.recipe = recipe     
		self.updateIsActive(conn, True)

		maxTime = 0
		pumpThreads = []
		for ing in recipe["drinkAmounts"]:
			for pump in self.pump_configuration.keys():
				if ing["drinkName"] == self.pump_configuration[pump]["value"]:
					waitTime = ing["amountInMilliliters"] * FLOW_RATE
					if (waitTime > maxTime):
						maxTime = waitTime
					print(f"Pouring '{ing['drinkName']}' for '{waitTime}' s")
					pump_t = threading.Thread(target=self.pour, args=(self.pump_configuration[pump]["pin"], waitTime))
					pumpThreads.append(pump_t)

		for thread in pumpThreads:
			thread.start()

		self.progressBar(conn, maxTime)

		# wait for threads to finish
		for thread in pumpThreads:
			thread.join()

		# sleep for a couple seconds to make sure the interrupts don't get triggered
		time.sleep(2)

		self.updateIsActive(conn, False)

	def left_btn(self, ctx):
		if not self.running:
			return # Here button action

	def right_btn(self, ctx):
		if not self.running:
			return # Here button action
    
	def onClientConnected(self, conn: socket, addr):
		print(f"Connected to client at: {addr}")
  
		conn.send((json.dumps({'command': 'ConnectedDrinks', 'data': self.pump_configuration}) + "\r\n").encode())
		while self.isRunning:
			data = conn.recv(1024)
			if not data:
				break
			print(f"Data received from client ('{addr}'): {data}")
   
			jsonData = json.loads(data)
			command = jsonData["command"]
   
			if (command == "StartRecipe"):
				self.makeDrink(conn, jsonData["data"])
			
		print(f"Closed client connection: {addr}")
		conn.close()
    
	def run(self):
		self.startInterrupts()
  
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.bind((HOST, PORT))
			s.listen()

			self.isRunning = True
			while self.isRunning:
				try:
					conn, addr = s.accept()
					client = threading.Thread(target=self.onClientConnected, args=(conn, addr))
					client.start()
				except KeyboardInterrupt:
					self.isRunning = False
					print("Shutting down...") # Consider sending close to all clients
				except:
					print("Exception in client connection caught")
     
			s.shutdown(socket.SHUT_RDWR)
			s.close()
   
		GPIO.cleanup()           # clean up GPIO on normal exit
		traceback.print_exc()


bartender = Bartender()
bartender.run()