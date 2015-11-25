__author__ = 'HM'
from scrapy.spiders import BaseSpider
from scrapy.selector import HtmlXPathSelector
from city_location.items import CityLocationItem


class MySpider(BaseSpider):
    name = "citylocation"
    allowed_domains = ["https://en.wikipedia.org"]
    start_urls = ["https://en.wikipedia.org/wiki/List_of_cities_by_latitude"]

    def parse(self, response):
        hxs = HtmlXPathSelector(response)
        data = hxs.select("//*[@id='mw-content-text']/table/tr")
        items = []

        for data1 in data:
            item = CityLocationItem()
            item["city_latitude"] = data1.select("td[1]/text()").extract()
            item["city_longitude"] = data1.select("td[2]/text()").extract()
            item["city_name"] = data1.select("td[3]/a/text()").extract()
            items.append(item)
        return items
