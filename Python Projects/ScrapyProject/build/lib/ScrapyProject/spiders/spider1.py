#!/usr/bin/env python
# -*- coding: utf-8 -*-
import scrapy
import requests
import re
from ..items import ScrapyprojectItem


class Spider1Spider(scrapy.Spider):
    name = 'spider1'
    page_number = 2
    payload = {'api_key': '24f80459581c7a89a60b0c9865200f8d', 'url': 'https://httpbin.org/ip'}
    r = requests.get('http://api.scraperapi.com', params=payload)
    print(r.text)
    keyword_file = open("/var/www/html/keyfolder/keywords.txt", "r+")
    all_key_words = keyword_file.readlines()
    keyword_file.close()
    all_links = []
    for keyword in all_key_words:
        formatted_keyword = keyword.replace('\n', '')
        formatted_keyword = formatted_keyword.strip()
        formatted_keyword = formatted_keyword.replace(' ', '+')
        all_links.append("http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=https://www.amazon.com/s?k=" +
            formatted_keyword + "&ref=nb_sb_noss_2")
    start_urls = all_links

    def parse(self, response):
        print("========== starting parse ===========")

        #all_containers = response.css("div.s-item-container")
       # if all_containers is None:
        #    all_containers = response.css(".sg-col-inner")

        all_containers = response.css("div.sg-col-inner")
        for shirts in all_containers:
            next_page = shirts.css('.a-link-normal::attr(href)').extract_first()
            if next_page is not None:
                if "https://www.amazon.com" not in next_page:
                    next_page = "https://www.amazon.com" + next_page
                yield scrapy.Request('http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=' +
			next_page, callback=self.parse_dir_contents)

        second_page = response.css('li.a-last a::attr(href)').get()
        if second_page is not None and Spider1Spider.page_number < 3:
            Spider1Spider.page_number += 1
            yield response.follow(second_page, callback=self.parse)

    def parse_dir_contents(self, response):
	items = ScrapyprojectItem()
	print("============= parsing page ==============")
	temp = response.css('#productTitle::text').extract()
	product_name = ''.join(temp)
	product_name = product_name.replace('\n', '')
	product_name = product_name.strip()
	temp = response.css('#priceblock_ourprice::text').extract()
	product_price = ''.join(temp)
	product_price = product_price.replace('\n', '')
	product_price = product_price.strip()
	temp = response.css('#SalesRank::text').extract()
	product_score = ''.join(temp)
	product_score = product_score.strip()
	product_score = re.sub(r'\D', '', product_score)
	product_ASIN = re.search(r'(?<=/)B[A-Z0-9]{9}', response.url)
	product_ASIN = product_ASIN.group(0)
	print("===================" + product_ASIN)
	#product_ASIN = ''.join(product_ASIN)
	#product_ASIN = product_ASIN.replace("http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=", "")
	items['product_ASIN'] = product_ASIN
	items['product_name'] = product_name
	items['product_price'] = product_price
	items['product_score'] = product_score
	yield items
