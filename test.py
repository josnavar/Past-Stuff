from keras.models import Sequential, Model
import keras
from keras.layers import Flatten, Dense, Dropout, Reshape, Permute, Activation, Input, merge
from keras.layers.convolutional import Convolution2D, MaxPooling2D, ZeroPadding2D
from keras.optimizers import SGD, Adadelta
import numpy as np
from scipy.misc import imread, imresize,imsave
import os
from keras.preprocessing.image import ImageDataGenerator
from keras.callbacks import ModelCheckpoint

def process_Image():
    listOfImages=np.zeros((2264,3,40,40),dtype="uint8")
    feed=[]
    labels=[]

    validationFeed=[]
    validationLabels=[]
    listOfVals=np.zeros([150,3,40,40],dtype="uint8")
    #Directories 
    #/home/pepe/Desktop/Monk
    #/home/pepe/Desktop/None-Monk
    
    for paths in os.listdir("/home/pepe/Desktop/Monk/"):
        image=imread("/home/pepe/Desktop/Monk/"+paths,mode="RGB")
        feed.append(image)
    for paths in os.listdir("/home/pepe/Desktop/Door/"):
        image=imread("/home/pepe/Desktop/Door/"+paths,mode="RGB")
        feed.append(image)
    for paths in os.listdir("/home/pepe/Desktop/Varrock_Guard/"):
        image=imread("/home/pepe/Desktop/Varrock_Guard/"+paths,mode="RGB")
        feed.append(image)
    
    for paths in os.listdir("/home/pepe/Desktop/1/"):
        image=imread("/home/pepe/Desktop/1/"+paths,mode="RGB")
        validationFeed.append(image)
    for paths in os.listdir("/home/pepe/Desktop/3/"):
        image=imread("/home/pepe/Desktop/3/"+paths,mode="RGB")
        validationFeed.append(image)
    for paths in os.listdir("/home/pepe/Desktop/2/"):
        image=imread("/home/pepe/Desktop/2/"+paths,mode="RGB")
        validationFeed.append(image)
    #Labels for validation Set
    for x in range(0,50):
        #Monk dir
        validationLabels.append(np.array([1.0,0.0,0.0]))
    for x in range(0,50):
        #Door dir
        validationLabels.append(np.array([0.0,1.0,0.0]))
    for x in range(0,50):
        #Varrock Guard dir
        validationLabels.append(np.array([0.0,0.0,1.0]))
        
    for x in range(0,150):
        for y in range(0,3):
            listOfVals[x][y]=validationFeed[x][:,:,y]
            
    validationLabels=np.stack(validationLabels)
    print validationLabels.shape
    print listOfVals.shape
    print listOfVals[0]
    print "***************"
    
    for x in range(0,2264):
        for y in range(0,3):
            listOfImages[x][y]=feed[x][:,:,y]
    
    
    #Labels for monk and none monk respectively.
    for x in range(0,751):
        #Monk dir
        labels.append(np.array([1.0,0.0,0.0]))
    for x in range(0,755):
        #Door dir
        labels.append(np.array([0.0,1.0,0.0]))
    for x in range(0,758):
        #Varrock Guard dir
        labels.append(np.array([0.0,0.0,1.0]))
    
        
    labels=np.stack(labels)
    print labels.shape
    print listOfImages.shape
    print listOfImages[0]
    augmentation=ImageDataGenerator(
        rotation_range=30,
        height_shift_range=0.2,
        width_shift_range=0.2,
        horizontal_flip=True,
        vertical_flip=True)
    
    sgd=SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
    checkpointer = ModelCheckpoint(filepath="harambe1.h5", verbose=1, save_best_only=True)
    ada=Adadelta()
    model=VGG_16()
    model.compile(optimizer=sgd,loss="categorical_crossentropy",metrics=["accuracy"])
    model.fit_generator(augmentation.flow(listOfImages,labels, batch_size=64),nb_epoch=50,samples_per_epoch=2264,validation_data=(listOfVals,validationLabels),callbacks=[checkpointer])
    #model.fit(listOfImages,labels,nb_epoch=25,batch_size=64)
    model.save("harambe2.h5")
    
#Modified model
def VGG_16(weights_path=None):
    model=Sequential()
    model.add(Convolution2D(64, 3, 3, border_mode='valid', input_shape=(3, 40, 40),init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('relu'))
    model.add(Convolution2D(64, 3, 3,init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.5))
    model.add(Convolution2D(128, 3, 3, border_mode='valid',init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('relu'))
    model.add(Convolution2D(128, 3, 3,init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.5))
    model.add(Flatten())
    # Note: Keras does automatic shape inference.
    model.add(Dense(500,init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('relu'))
    model.add(Dropout(0.5))

    model.add(Dense(3,init="glorot_uniform"))
    model.add(keras.layers.normalization.BatchNormalization())
    model.add(Activation('softmax'))


    return model
process_Image()
