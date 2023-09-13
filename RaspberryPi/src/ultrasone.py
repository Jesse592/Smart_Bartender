import time
import ctypes
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)

class Ultrasone():
	def __init__(self, triggerPin, echoPin):
		self.triggerPin = triggerPin
		self.echoPin = echoPin
		GPIO.setup(self.echoPin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
		GPIO.setup(self.triggerPin, GPIO.OUT, initial=GPIO.LOW)
       
	def trigger(self):		
		GPIO.output(self.triggerPin, GPIO.HIGH)
		
		libc = ctypes.CDLL('libc.so.6')
		libc.usleep(1)

		GPIO.output(self.triggerPin, GPIO.LOW)

		while GPIO.input(self.echoPin) == 0:
			pulse_start = time.time()
		while GPIO.input(self.echoPin) == 1:
			pulse_end = time.time()
   
		pulse_duration = pulse_end - pulse_start
		distance = pulse_duration * 17165
		distance = round(distance, 1)
		self.distance = distance