# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy
from scrapy.item import Item, Field


class CityLocationItem(scrapy.Item):
    city_latitude = Field()
    city_longitude = Field()
    city_name = Field()
