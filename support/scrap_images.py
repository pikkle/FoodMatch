import csv
from multiprocessing.pool import Pool
from urllib.request import urlopen
import MySQLdb

from bs4 import BeautifulSoup

dishes = []

class Dish:

    def __init__(self, name, img_url):
        self.name = name
        self.img_url = img_url

    def __str__(self):
        return "Name: " + self.name + " image: " + self.img_url


# max = 5
# count = 0
# with open('raw_metadata.csv', 'r') as csvfile:
#     spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
#
#     for row in spamreader:
#         if (count > max):
#             break
#         if (count != 0 and len(row) > 2):
#             print(row)
#             url = row[1]
#             page = urlopen(url)
#             soup = BeautifulSoup(page, "lxml")
#             imgElem = soup.find('img', class_='recipe-main-img')
#             if imgElem:
#                 print(imgElem["data-src"])
#                 name = str(row[2]).split(' - ')
#                 print(name)
#                 dishes.append(Dish(str(row[2]), str(imgElem["data-src"])))
#             else:
#                 print("No image for id " + row[0])
#         count += 1


db = MySQLdb.Connect(host="remote.limayankee.com", user="foodmatch", passwd="foodmatch2017", db="foodmatch")


def add_dish_to_db(dish):
    try:
        c = db.cursor()
        c.execute("INSERT INTO dishes VALUES (NULL,%s, %s,0,1);", (dish.name, dish.img_url))
        db.commit()
    except:
        print("Not Ok")
        return


def getDatas(row):
    try:
        if len(row) > 2 and row[1] != 'url':
            url = row[1]
            page = urlopen(row[1])
            soup = BeautifulSoup(page, "lxml")
            imgElem = soup.find('img', class_='recipe-main-img')

            if imgElem:
                print(imgElem["data-src"])
                name = str(row[2]).split(' - ')
                dish = Dish(name[0], str(imgElem["data-src"]))
                print(name)
                add_dish_to_db(dish)
                return dish
            else:
                print("No image for id " + row[0])
    except:
        return
    return


if __name__ == '__main__':

    with open('raw_metadata.csv', 'r') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')

        # new_array = []
        # count = 0
        # for row in spamreader:
        #     new_array.append(row)
        #     count += 1
        #     if count == 100:
        #         break

        p = Pool(4)
        dishes = p.map(getDatas, spamreader)
        dishes = [i for i in dishes if i is not None]
        p.close()



