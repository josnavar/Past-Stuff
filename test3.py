import pyautogui
from keras.models import load_model
from scipy.misc import imread, imshow
import numpy as np
from numpy import linalg as LA
import math
from PIL import Image

def computeMean(someList,iterations):
    stackArray=np.stack(someList)
    
    mean=np.mean(stackArray,axis=0)
    if (iterations==1):
        return np.asarray(mean)
        
    
        
    newList=[]
    
    
    for x in stackArray:
        if LA.norm(x-mean)<=boxSize:
            newList.append(x)
    return computeMean(newList,iterations-1)
    
boxSize=56.0
model=load_model("harambe0.h5")
#Do ten times just for lulz
for x in range(0,10):
    image=pyautogui.screenshot(region=(337,28,511,334))
    possiblePixels=[]
    for width in range(0,511):
        for height in range(0,334):
            red, blue, green=image.getpixel((width,height))
            red=red*1.0
            blue=blue*1.0
            green=green*1.0
            if (red>=2.0*blue and red>=2*green and red>100):
                possiblePixels.append(np.asarray((width,height)))
                #Possible Monk
    
    candidate=computeMean(possiblePixels,2)
    if (candidate is None):
        print "it's none"
        continue
        
    candidateX=int(candidate[0])
    candidateY=int(candidate[1])
    print image.getpixel((candidateX,candidateY))
    print candidateX
    print candidateY
    
    
    
    candidateImage=image.crop(box=(candidateX-20,candidateY-20,candidateX+20,candidateY+20))
    imageList=list(candidateImage.getdata())
    
    
    image=np.array(candidateImage)
    
    a=np.zeros([1,3,40,40])
    a[0][0]=image[:,:,0]
    a[0][1]=image[:,:,1]
    a[0][2]=image[:,:,2]

    prediction=model.predict(a)
    print prediction
    if (prediction[0][0]>2*prediction[0][2] and prediction[0][0]>2*prediction[0][3]):
        #Move Mouse
        pyautogui.moveTo(candidateX+337,candidateY+28)
    else:
        continue
    
    

    

        
                
            
