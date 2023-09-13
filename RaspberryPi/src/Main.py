import time
import json
import threading
import traceback
import socket
import RPi.GPIO as GPIO
from menu import Menu
from ultrasone import Ultrasone

HOST = "192.168.4.1"
#HOST = "145.49.12.93"
PORT = 65432

GPIO.setmode(GPIO.BCM)

FLOW_RATE = 60.0/500.0
ULTRASONE_TRIGGER_DELAY = 1

class Bartender(): 
	def __init__(self):     
		self.menu = Menu()
		self.ultrasone = Ultrasone(4,3)
		self.running = False
  
		self.recipe = None
  
		# load the pump configuration from file
		self.pump_configuration = Bartender.readPumpConfiguration()
  
		for pump in self.pump_configuration.keys():
			GPIO.setup(self.pump_configuration[pump]["pin"], GPIO.OUT, initial=GPIO.LOW)

		print("Done initializing")

	@staticmethod
	def readPumpConfiguration():
		return json.load(open('pump_config.json'))

	@staticmethod
	def writePumpConfiguration(configuration):
		with open("pump_config.json", "w") as jsonFile:
			json.dump(configuration, jsonFile)

	def clean(self, conn, pump):
		self.updateIsActive(conn, True)
  
		while (self.isCleaning):
			print(f"Cleaning...")
			self.pour(pump["key"], 1)
		
		self.updateIsActive(conn, False)

	def updateIsActive(self, conn: socket, running, progress = None):
		self.running = running

		conn.send((json.dumps({'command': 'RecipeChanged', 'data': {'isProcessing': running, 'progress': progress, 'recipe': self.recipe}}) + "\r\n").encode())

	def pour(self, pin, waitTime):
		GPIO.output(pin, GPIO.HIGH)
		time.sleep(waitTime)
		GPIO.output(pin, GPIO.LOW)

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
		self.menu.setText("Connected")
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
			elif (command == "RunPump"):
				if (jsonData["data"]["running"]):
					self.isCleaning = True
					threading.Thread(target=self.clean, args=(conn, jsonData["data"]["pump"])).start()
				else:
					self.isCleaning = False
			
		print(f"Closed client connection: {addr}")
		conn.close()
	
	def runUltrasone(self):
		self.ultrasone.distance = 0
		try:
			while self.isRunning:
				self.ultrasone.trigger()
				self.menu.setText(f"{self.ultrasone.distance}cm")
				time.sleep(ULTRASONE_TRIGGER_DELAY)
		except:
			self.ultrasone.distance = 0
    
	def run(self):
		self.isRunning = True
     
		self.menu.setText("Smartbartender")
		threading.Thread(target=self.runUltrasone).start()

		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.bind((HOST, PORT))
			s.listen()

			while self.isRunning:
				try:
					conn, addr = s.accept()
					client = threading.Thread(target=self.onClientConnected, args=(conn, addr))
					client.start()
				except KeyboardInterrupt:
					self.isRunning = False
					print("Shutting down...") # Consider sending close to all clients
				except Exception:
					print("Exception in client connection caught")
     
			s.shutdown(socket.SHUT_RDWR)
			s.close()
   
		GPIO.cleanup()           # clean up GPIO on normal exit
		traceback.print_exc()


bartender = Bartender()
bartender.run()