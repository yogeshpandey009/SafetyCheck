__author__ = 'HM'
import csv

with open('items.csv', 'rb') as f:
    reader = csv.reader(f)
    c = csv.writer(open("items_new.csv", "wb"))
    try:
        for row in reader:
            if row[0] != '':
                if row[1] != '':
                    y1 =  row[1].partition("\xc2\xb0")
                    y2 = y1[2].partition('\xe2\x80\xb2')
                    if(y2[2] is 'W'):
                        y = '-'+y1[0]+'.'+y2[0]
                    else:
                        y = y1[0]+'.'+y2[0]
                if row[2] != '':
                    x1 =  row[2].partition("\xc2\xb0")
                    x2 = x1[2].partition('\xe2\x80\xb2')
                    if(x2[2] is 'S'):
                        x = '-'+x1[0]+'.'+x2[0]
                    else:
                        x = x1[0]+'.'+x2[0]
                xy = row[0],x,y
                print xy
                c.writerow([row[0],x,y])
    finally:
        f.close()

