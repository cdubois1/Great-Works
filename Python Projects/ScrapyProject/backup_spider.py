#!/usr/bin/env python
# -*- coding: utf-8 -*-
import scrapy
import requests
import re
from time import sleep
from ..items import ScrapyprojectItem


class Spider1Spider(scrapy.Spider):
    name = 'spider1'
    page_number = 2
    current_keyword = 0
    payload = {'api_key': '24f80459581c7a89a60b0c9865200f8d', 'url': 'https://httpbin.org/ip'}
    r = requests.get('http://api.scraperapi.com', params=payload)
    print(r.text)
    
    with open("/var/www/html/keyfolder/db_keywords.txt") as f:
        seen = set()
        for line in f:
            line_lower = line.lower()
            seen.add(line_lower)
    with open("/var/www/html/keyfolder/keywords.txt") as f:
        with open("/var/www/html/keyfolder/db_keywords.txt", "a") as f1:
            for line in f:
                if line not in seen:
                    line = line.lower()
                    if "\n" in line:
                        f1.write(line)
                    else:
                        f1.write(line)
                        f1.write("\n")
                        

    keyword_file = open("/var/www/html/keyfolder/keywords.txt", "r+")
    keyword_file.seek(0, 0)
    all_key_words = keyword_file.readlines()
    keyword_file.close()
    all_links = []
    keyword_list = []
    for keyword in all_key_words:
        keyword_list.append(keyword)
        formatted_keyword = keyword.replace('\n', '')
        formatted_keyword = formatted_keyword.strip()
        formatted_keyword = formatted_keyword.replace(' ', '+')
        all_links.append("http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=https://www.amazon.com/s?k=" + formatted_keyword + "&ref=nb_sb_noss_2")
    
    start_urls = all_links

    def parse(self, response):
        print("========== starting parse ===========")
        print(response.text)

        #all_containers = response.css("div.s-item-container")
       # if all_containers is None:
        #    all_containers = response.css(".sg-col-inner")

        all_containers = response.css("div.sg-col-inner")
        for shirts in all_containers:
            next_page = shirts.css('.a-link-normal::attr(href)').extract_first()
            if next_page is not None:
                if "https://www.amazon.com" not in next_page:
                    next_page = "https://www.amazon.com" + next_page
                yield response.follow('http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=' +
			next_page, callback=self.parse_dir_contents)

        sleep(3)
        second_page = response.css('li.a-last a::attr(href)').get()
        if second_page is not None and Spider1Spider.page_number < 4:
            Spider1Spider.page_number += 1
            yield response.follow(second_page, callback=self.parse)
        else:
            Spider1Spider.current_keyword = Spider1Spider.current_keyword + 1

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
	product_keyword = Spider1Spider.keyword_list[Spider1Spider.current_keyword]
        product_keyword = product_keyword[:-2].lower()
        items['product_keyword'] = product_keyword
	items['product_ASIN'] = product_ASIN
	items['product_name'] = product_name
	items['product_price'] = product_price
	items['product_score'] = product_score
	yield items
