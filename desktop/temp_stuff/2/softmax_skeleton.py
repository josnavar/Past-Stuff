from utils import *
import numpy as np
import matplotlib.pyplot as plt
import math
def augmentFeatureVector(X):
    columnOfOnes = np.zeros([len(X), 1]) + 1
    return np.hstack((columnOfOnes, X))

def computeProbabilities(X, theta):
    #Implement softmax equation, first find out what theta is, bihhh theta is just our normal classifier styled theta!
    #First calculate SUM(e^(theta^T*x))
    rows=theta.shape[0]
    columns=X.shape[0]
    outArray=np.zeros((rows,columns)) #k*n matrix
    for x in range(X.shape[0]):
        counter=0
        for y in range(theta.shape[0]):
            counter=counter+math.exp(np.dot(theta[y],X[x]))
        for y in range(theta.shape[0]):
            elt=math.exp(np.dot(theta[y],X[x]))*1.0
            elt=elt/counter
            outArray[y][x]=elt
    return outArray

def computeCostFunction(X, Y, theta, lambdaFactor):
    probArray=computeProbabilities(X,theta)
    cost1=0
    for eltx in range(X.shape[0]):
        for elty in range(theta.shape[0]):
            if (Y[eltx]==elty):
                cost1=cost1+math.log(probArray[elty][eltx])
    cost1=1.0*cost1/(-X.shape[0])
    cost2=0
    for x in range(theta.shape[0]):
        for y in range(theta.shape[1]):
            cost2=cost2+theta[x][y]**2
    cost2=cost2*lambdaFactor/2.0
    return cost1+cost2
            

def runGradientDescentIteration(X, Y, theta, alpha, lambdaFactor):
    resultTheta=np.zeros((theta.shape[0],theta.shape[1]))
    probArray=computeProbabilities(X,theta)
    for elty in range(theta.shape[0]):
        result=np.zeros(theta.shape[1])
        for eltx in range(X.shape[0]):
            if (Y[eltx]==elty):
                result=result+X[eltx]*(1-(probArray[elty][eltx]))
            else:
                result=result+X[eltx]*(-(probArray[elty][eltx]))
        result=-result*1.0/(X.shape[0])
        result=result+lambdaFactor*theta[elty]
        resultTheta[elty]=theta[elty]-alpha*result
    return resultTheta
            
            

def softmaxRegression(X, Y, alpha, lambdaFactor, k, numIterations):
    X = augmentFeatureVector(X)
    theta = np.zeros([k, X.shape[1]])
    costFunctionProgression = []
    for i in range(numIterations):
        print i
        costFunctionProgression.append(computeCostFunction(X, Y, theta, lambdaFactor))
        theta = runGradientDescentIteration(X, Y, theta, alpha, lambdaFactor)
    return theta, costFunctionProgression
    
def getClassification(X, theta):
    X = augmentFeatureVector(X)
    probabilities = computeProbabilities(X, theta)
    return np.argmax(probabilities, axis = 0)

def plotCostFunctionOverTime(costFunctionHistory):
    plt.plot(range(len(costFunctionHistory)), costFunctionHistory)
    plt.ylabel('Cost Function')
    plt.xlabel('Iteration number')
    plt.show()


def computeTestError(X, Y, theta):
    errorCount = 0.
    assignedLabels = getClassification(X, theta)
    return 1 - np.mean(assignedLabels == Y)
