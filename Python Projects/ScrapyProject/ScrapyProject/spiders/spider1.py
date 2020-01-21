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
    payload = {'api_key': '24f80459581c7a89a60b0c9865200f8d', 'url': 'https://httpbin.org/ip'}
    r = requests.get('http://api.scraperapi.com', params=payload)
    print(r.text)
    
    f = open("/var/www/html/keyfolder/pagination.txt", "r")
    pagination_num = f.readline()
    f.close()

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
    
    def start_requests(self):
        with open("/var/www/html/keyfolder/keywords.txt") as f:
            for index, line in enumerate(f):
                try:
                    keyword = line.strip()
                    formatted_keyword = keyword.replace(' ', '+')
                    url = 'http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=https://www.amazon.com/s?k=' + formatted_keyword + '&ref=nb_sb_noss_2'
                    request = scrapy.Request(url, meta={'priority': index}, cb_kwargs=dict(keyword=keyword), dont_filter=True)
                    request.cb_kwargs['page_number'] = 1
                    yield request
                except:
                    continue
                    
                    
    def parse(self, response, keyword, page_number):
        print("========== starting parse ===========")

        for next_page in response.css("h2.a-size-mini a").xpath("@href").extract():
            if next_page is not None:
                if "https://www.amazon.com" not in next_page:
                    next_page = "https://www.amazon.com" + next_page
                request = scrapy.Request('http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=' + next_page, callback=self.parse_dir_contents, dont_filter=True)
                request.cb_kwargs['keyword'] = keyword
                yield request

        second_page = response.css('li.a-last a').xpath("@href").extract_first()
        if second_page is not None and page_number < Spider1Spider.pagination_num:
            page_number = page_number + 1
            if "https://www.amazon.com" not in second_page:
                second_page = "https://www.amazon.com" + second_page
            request = scrapy.Request('http://api.scraperapi.com/?api_key=24f80459581c7a89a60b0c9865200f8d&url=' + second_page, cb_kwargs=dict(keyword=keyword), dont_filter=True, callback=self.parse)
            request.cb_kwargs['page_number'] = page_number
            yield request

    def parse_dir_contents(self, response, keyword):
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
        product_keyword = keyword
        items['product_keyword'] = product_keyword
	items['product_ASIN'] = product_ASIN
	items['product_name'] = product_name
	items['product_price'] = product_price
	items['product_score'] = product_score
	yield items
