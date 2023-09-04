import time
import json
import threading
import traceback
import socket

#HOST = "192.168.178.213"  # Make IP static or update field dynamicly
HOST = "145.49.41.13"
PORT = 65432

FLOW_RATE = 60.0/100.0

class Bartender(): 
	def __init__(self):
		self.running = False

		# load the pump configuration from file
		self.pump_configuration = Bartender.readPumpConfiguration()
  
		# for pump in self.pump_configuration.keys(): TODO: Enable on Pi
			# GPIO.setup(self.pump_configuration[pump]["pin"], GPIO.OUT, initial=GPIO.HIGH)

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
		#GPIO.add_event_detect(self.btn1Pin, GPIO.FALLING, callback=self.left_btn, bouncetime=LEFT_PIN_BOUNCE)  TODO: Enable on Pi
		#GPIO.add_event_detect(self.btn2Pin, GPIO.FALLING, callback=self.right_btn, bouncetime=RIGHT_PIN_BOUNCE)  

	def stopInterrupts(self):
		print("Button interupt stoped")
		#GPIO.remove_event_detect(self.btn1Pin) TODO: Enable on Pi
		#GPIO.remove_event_detect(self.btn2Pin)

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

		self.running = False


	def pour(self, pin, waitTime):
		#GPIO.output(pin, GPIO.LOW) TODO: Enable on Pi
		time.sleep(waitTime)
		#GPIO.output(pin, GPIO.HIGH)

	def progressBar(self, waitTime):
		interval = waitTime / 100.0
		for x in range(1, 101):
			self.led.clear_display()
			self.updateProgressBar(x, y=35)
			self.led.display()
			time.sleep(interval)

	def makeDrink(self, ingredients):
		self.running = True

		maxTime = 0
		pumpThreads = []
		for ing in ingredients.keys():
			for pump in self.pump_configuration.keys():
				if ing == self.pump_configuration[pump]["value"]:
					waitTime = ingredients[ing] * FLOW_RATE
					if (waitTime > maxTime):
						maxTime = waitTime
					pump_t = threading.Thread(target=self.pour, args=(self.pump_configuration[pump]["pin"], waitTime))
					pumpThreads.append(pump_t)

		for thread in pumpThreads:
			thread.start()

		self.progressBar(maxTime)

		# wait for threads to finish
		for thread in pumpThreads:
			thread.join()

		# sleep for a couple seconds to make sure the interrupts don't get triggered
		time.sleep(2)

		self.running = False

	def left_btn(self, ctx):
		if not self.running:
			self.menuContext.advance()

	def right_btn(self, ctx):
		if not self.running:
			self.menuContext.select()

	def updateProgressBar(self, percent, x=15, y=15):
		height = 10
		width = self.screen_width-2*x
		for w in range(0, width):
			self.led.draw_pixel(w + x, y)
			self.led.draw_pixel(w + x, y + height)
		for h in range(0, height):
			self.led.draw_pixel(x, h + y)
			self.led.draw_pixel(self.screen_width-x, h + y)
			for p in range(0, percent):
				p_loc = int(p/100.0*width)
				self.led.draw_pixel(x + p_loc, h + y)
    
	def onClientConnected(self, conn, addr):
		print(f"Connected to client at: {addr}")
		conn.send((json.dumps(self.pump_configuration) + "\r\n").encode())
		while True:
			data = conn.recv(1024)
			if not data:
				break
			conn.sendall(data)
		print(f"Closed client connection: {addr}")
		conn.close()
    
	def run(self):
		self.startInterrupts()

		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.bind((HOST, PORT))
			s.listen()
			while True:	
				conn, addr = s.accept()
				threading.Thread(target=self.onClientConnected, args=(conn, addr)).start()				
        
		# except KeyboardInterrupt:  TODO: Enable on Pi
			#GPIO.cleanup()       # clean up GPIO on CTRL+C exit
		#GPIO.cleanup()           # clean up GPIO on normal exit 

		traceback.print_exc()


bartender = Bartender()
bartender.run()