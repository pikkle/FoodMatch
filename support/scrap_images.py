import csv
from urllib.request import urlopen

from bs4 import BeautifulSoup

max = 100
count = 0
with open('raw_metadata.csv', 'r') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')

    for row in spamreader:
        if (count > max):
            break
        if (count != 0 and len(row) > 2):
            url = row[1]
            page = urlopen(url)
            soup = BeautifulSoup(page, "lxml")
            imgElem = soup.find('img', class_='recipe-main-img')
            if imgElem:
                print(imgElem["data-src"])
            else:
                print("No image for id " + row[0])
        count += 1
