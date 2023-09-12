# SPDX-FileCopyrightText: 2021 ladyada for Adafruit Industries
# SPDX-License-Identifier: MIT

import board
import digitalio
from PIL import Image, ImageDraw, ImageFont
import adafruit_ssd1306

WIDTH = 128
HEIGHT = 64
BORDER = 5

class Menu():
	def __init__(self):
		oled_reset = digitalio.DigitalInOut(board.D26)
  
		spi = board.SPI()
		oled_cs = digitalio.DigitalInOut(board.D5)
		oled_dc = digitalio.DigitalInOut(board.D6)
		self.oled = adafruit_ssd1306.SSD1306_SPI(WIDTH, HEIGHT, spi, oled_dc, oled_reset, oled_cs)

	def setText(self, text):
		self.oled.fill(0)
		self.oled.show()

		image = Image.new("1", (self.oled.width, self.oled.height))
		draw = ImageDraw.Draw(image)

		# Load default font.
		font = ImageFont.truetype('DejaVuSerif.ttf', 16)

		(font_width, font_height) = font.getsize(text)
		draw.text(
    		(self.oled.width // 2 - font_width // 2, self.oled.height // 2 - font_height // 2),
    		text,
    		font=font,
    		fill=255,
		)

		self.oled.image(image)
		self.oled.show()
