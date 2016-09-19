import random as ra
import math
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as pat
from numpy import linalg as LA
from scipy.misc import logsumexp

#---------------------------------------------------------------------------------
# Utility Functions - There is no need to edit code in this section.
#---------------------------------------------------------------------------------

# Reads a data matrix from file.
# Output: X: data matrix.
def readData(file):
    X = []
    with open(file,"r") as f:
        for line in f:
            X.append(map(float,line.split(" ")))
    return np.array(X)
    

# plot 2D toy data
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
#        Label: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
#        title: a string represents the title for the plot
def plot2D(X,K,Mu,P,Var,Label,title):
    r=0.25
    color=["r","b","k","y","m","c"]
    n,d = np.shape(X)
    per= Label/(1.0*np.tile(np.reshape(np.sum(Label,axis=1),(n,1)),(1,K)))
    fig=plt.figure()
    plt.title(title)
    ax=plt.gcf().gca()
    ax.set_xlim((-20,20))
    ax.set_ylim((-20,20))
    for i in xrange(len(X)):
        angle=0
        for j in xrange(K):
            cir=pat.Arc((X[i,0],X[i,1]),r,r,0,angle,angle+per[i,j]*360,edgecolor=color[j])
            ax.add_patch(cir)
            angle+=per[i,j]*360
    for j in xrange(K):
        sigma = np.sqrt(Var[j])
        circle=plt.Circle((Mu[j,0],Mu[j,1]),sigma,color=color[j],fill=False)
        ax.add_artist(circle)
        text=plt.text(Mu[j,0],Mu[j,1],"mu=("+str("%.2f" %Mu[j,0])+","+str("%.2f" %Mu[j,1])+"),stdv="+str("%.2f" % np.sqrt(Var[j])))
        ax.add_artist(text)
    plt.axis('equal')
    plt.show()

#---------------------------------------------------------------------------------



#---------------------------------------------------------------------------------
# K-means methods - There is no need to edit code in this section.
#---------------------------------------------------------------------------------

# initialization for k means model for toy data
# input: X: n*d data matrix;
#        K: number of mixtures;
#        fixedmeans: is an optional variable which is
#        used to control whether Mu is generated from a deterministic way
#        or randomized way
# output: Mu: K*d matrix, each row corresponds to a mixture mean;
#         P: K*1 matrix, each entry corresponds to the weight for a mixture;
#         Var: K*1 matrix, each entry corresponds to the variance for a mixture;    
def init(X,K,fixedmeans=False):
    n, d = np.shape(X)
    P=np.ones((K,1))/float(K)

    if (fixedmeans):
        assert(d==2 and K==3)
        Mu = np.array([[4.33,-2.106],[3.75,2.651],[-1.765,2.648]])
    else:
        # select K random points as initial means
        rnd = np.random.rand(n,1)
        ind = sorted(range(n),key = lambda i: rnd[i])
        Mu = np.zeros((K,d))
        for i in range(K):
            Mu[i,:] = np.copy(X[ind[i],:])

    Var=np.mean( (X-np.tile(np.mean(X,axis=0),(n,1)))**2 )*np.ones((K,1))
    return (Mu,P,Var)


# K Means method
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output: Mu: K*d matrix, each row corresponds to a mixture mean;
#         P: K*1 matrix, each entry corresponds to the weight for a mixture;
#         Var: K*1 matrix, each entry corresponds to the variance for a mixture;
#         post: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
def kMeans(X, K, Mu, P, Var):
    prevCost=-1.0; curCost=0.0
    n=len(X)
    d=len(X[0])
    while abs(prevCost-curCost)>1e-4:
        post=np.zeros((n,K))
        prevCost=curCost
        #E step
        for i in xrange(n):
            post[i,np.argmin(np.sum(np.square(np.tile(X[i,],(K,1))-Mu),axis=1))]=1
        #M step
        n_hat=np.sum(post,axis=0)
        P=n_hat/float(n)
        curCost = 0
        for i in xrange(K):
            Mu[i,:]= np.dot(post[:,i],X)/float(n_hat[i])
            # summed squared distance of points in the cluster from the mean
            sse = np.dot(post[:,i],np.sum((X-np.tile(Mu[i,:],(n,1)))**2,axis=1))
            curCost += sse
            Var[i]=sse/float(d*n_hat[i])
        print curCost
    # return a mixture model retrofitted from the K-means solution
    return (Mu,P,Var,post) 
#---------------------------------------------------------------------------------


def distribution(x,mu,var):
    result=1.0/((2*math.pi*(var))**(np.shape(x)[0]/2))
    temp1=x-mu
    temp2=np.transpose(temp1)
    result2=math.exp((-1/(2*(var)))*np.dot(temp2,temp1))
    result=result*result2
    return result
def totalProb(x,mu,var,p):
    result=0
    for elt in range(0,len(p)):
        result=result+p[elt]*distribution(x,mu[elt],var[elt])
    return result
#---------------------------------------------------------------------------------
# PART 1 - EM algorithm for a Gaussian mixture model
#---------------------------------------------------------------------------------

# E step of EM algorithm
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output:post: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
#        LL: a Loglikelihood value
def Estep(X,K,Mu,P,Var):
    n,d = np.shape(X) # n data points of dimension d
    post = np.zeros((n,K)) # posterior probabilities to compute
    LL = 0.0    # the LogLikelihood

    #Write your code here
    for clusters in range(0,len(P)):
        for points in range(0,len(X)):
            pjt=P[clusters]*distribution(X[points],Mu[clusters],Var[clusters])
            pjt=pjt/(totalProb(X[points],Mu,Var,P))
	    post[points][clusters]=pjt
    logResult=0
    for elt in X:
        logResult=logResult+math.log(totalProb(elt,Mu,Var,P))
    LL=logResult       
    return (post,LL)


# M step of EM algorithm
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
#        post: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
# output:Mu: updated Mu, K*d matrix, each row corresponds to a mixture mean;
#        P: updated P, K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: updated Var, K*1 matrix, each entry corresponds to the variance for a mixture;
def Mstep(X,K,Mu,P,Var,post):
    n,d = np.shape(X) # n data points of dimension d
    presult=np.zeros((K,1))
    muresult=np.zeros((K,d))
    varresult=np.zeros((K,1))    
    for y in range(K):
        temp=0
        temp1=0
        for x in range(n):
            temp=temp+post[x][y]
            temp1=temp1+post[x][y]*X[x] 
        presult[y]=1.0*temp/n
        muresult[y]=1.0*temp1/temp
    #print Mu
    for y in range(K):
        temp2=0
        temp=0
        for x in range(n):
            deez=np.transpose(X[x]-muresult[y])
            nuts=X[x]-muresult[y]
            temp=temp+post[x][y]
            temp2=temp2+post[x][y]*np.dot(deez,nuts)
        varresult[y]=1.0*temp2/(d*temp)
    return (muresult,presult,varresult)


# Mixture of Guassians
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output: Mu: updated Mu, K*d matrix, each row corresponds to a mixture mean;
#         P: updated P, K*1 matrix, each entry corresponds to the weight for a mixture;
#         Var: updated Var, K*1 matrix, each entry corresponds to the variance for a mixture;
#         post: updated post, n*K matrix, each row corresponds to the soft counts for all mixtures for an example
#         LL: Numpy array for Loglikelihood values
def mixGauss(X,K,Mu,P,Var):
    n,d = np.shape(X) # n data points of dimension d
    post = np.zeros((n,K)) # posterior probabilities
    MuP=Mu
    PP=P
    VarP=Var

    a=0
    record=[]
    while True: 
        (a,b)=Estep(X,K,MuP,PP,VarP)
        if len(record)>1 and b-record[-1]<=(10)**(-6)*abs(b):
            break
        record.append(b)
        (MuP,PP,VarP)=Mstep(X,K,MuP,PP,VarP,a)
    record=np.asarray(record)
    return (MuP,PP,VarP,a,record)


# Bayesian Information Criterion (BIC) for selecting the number of mixture components
# input:  n*d data matrix X, a list of K's to try 
# output: the highest scoring choice of K
def BICmix(X,Kset):
    n,d = np.shape(X)
    Llist=[]
    #Initialize means and vars:
    for k in Kset:
        (Mu,P,Var) = init(X,k)
        (MuP,PP,VarP,post,LL)=mixGauss(X,k,Mu,P,Var)
        Llist.append(LL[-1]-0.5*(4)*math.log(n))
    #Write your code here
    stuff=max(Llist)
    K=Llist.index(stuff)+1
    return K
#---------------------------------------------------------------------------------



#---------------------------------------------------------------------------------
# PART 2 - Mixture models for matrix completion
#---------------------------------------------------------------------------------
def distribution2(x,mu,var):
    tempx=np.nonzero(x)
    newX=np.take(x,tempx)
    newMu=np.take(mu,tempx)
    temp1=newX-newMu
    temp2=np.transpose(temp1)
    result=-(np.shape(newX)[1]/2.0)*(math.log(2)+math.log(math.pi)+math.log(var))
    result2=(-1/(2*(var)))*np.dot(temp1,temp2)
    result=result+result2
    return result

def totalProb2(x,mu,var,p):
    result=0
    record=[]
    for elt in range(0,len(p)):
        result=math.log(p[elt])+(distribution2(x,mu[elt],var[elt]))
        record.append(result)
    return record


# RMSE criteria
# input: X: n*d data matrix;
#        Y: n*d data matrix;
# output: RMSE
def rmse(X,Y):
    return np.sqrt(np.mean((X-Y)**2))


# E step of EM algorithm with missing data
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output:post: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
#        LL: a Loglikelihood value
def Estep_part2(X,K,Mu,P,Var):
    n,d = np.shape(X) # n data points of dimension d
    post = np.zeros((n,K)) # posterior probabilities to compute
    LL = 0.0    # the LogLikelihood
    logResult=0
    for clusters in range(0,len(P)):
        for points in range(0,len(X)):
            f=math.log(P[clusters])+(distribution2(X[points],Mu[clusters],Var[clusters]))
            pup=f-logsumexp(totalProb2(X[points],Mu,Var,P))
            post[points][clusters]=math.exp(pup)
    for elt in range(len(X)):
        LL=LL+logsumexp(totalProb2(X[elt],Mu,Var,P,))
    return (post,LL)

	
# M step of EM algorithm
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
#        post: n*K matrix, each row corresponds to the soft counts for all mixtures for an example
# output:Mu: updated Mu, K*d matrix, each row corresponds to a mixture mean;
#        P: updated P, K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: updated Var, K*1 matrix, each entry corresponds to the variance for a mixture;
def Mstep_part2(X,K,Mu,P,Var,post, minVariance=0.25):
    n,d = np.shape(X) # n data points of dimension d
    newMu=np.zeros((K,d))
    newP=np.zeros((K,1))
    newVar=np.zeros((K,1))
    for clusters in range(K):
        temp=0
        temp=sum(post[:,clusters])
        newP[clusters]=math.exp(1.0*math.log(temp)-math.log(n))
    for clusters in range(K):
        for l in range(d):
            pepe=np.nonzero(X[:,l])
            pepex=np.take(X[:,l],pepe)
            pepemu=np.take((post[:,clusters]),pepe)
            temp=sum(pepemu[0])
            mean=np.dot(pepemu,np.transpose(pepex))
            if temp>=1:
                mean=(1.0*(mean)/(temp))
                newMu[clusters][l]=mean
            else:
                newMu[clusters][l]=Mu[clusters][l]
    for clusters in range(K):
        tempvar=0
        tempvar1=0
        for points in range(n):
            xtemp=np.nonzero(X[points])
            xP=np.take(X[points],xtemp)
            muP=np.take(newMu[clusters],xtemp)
            tempvar=tempvar+post[points][clusters]*np.dot((xP-muP),np.transpose(xP-muP))
            tempvar1=tempvar1+np.shape(xP)[1]*post[points][clusters]
        tempvar=(1.0*(tempvar)/(tempvar1))
        if (tempvar>=0.25):
            newVar[clusters]=tempvar
        else:
            newVar[clusters]=0.25
    return (newMu,newP,newVar)

	
# mixture of Guassians
# input: X: n*d data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output: Mu: updated Mu, K*d matrix, each row corresponds to a mixture mean;
#         P: updated P, K*1 matrix, each entry corresponds to the weight for a mixture;
#         Var: updated Var, K*1 matrix, each entry corresponds to the variance for a mixture;
#         post: updated post, n*K matrix, each row corresponds to the soft counts for all mixtures for an example
#         LL: Numpy array for Loglikelihood values
def mixGauss_part2(X,K,Mu,P,Var):
    n,d = np.shape(X) # n data points of dimension d
    post = np.zeros((n,K)) # posterior probabilities
    MuP=Mu
    PP=P
    VarP=Var
    #Write your code here
    #Use function Estep and Mstep as two subroutines
    a=0
    record=[]
    while True: 
        (a,b)=Estep_part2(X,K,MuP,PP,VarP)
        if len(record)>1 and b-record[-1]<=(10)**(-6)*abs(b):
            break
        record.append(b)
        (MuP,PP,VarP)=Mstep_part2(X,K,MuP,PP,VarP,a,0.25)
    record=np.asarray(record)
    return (MuP,PP,VarP,a,record)


# fill incomplete Matrix
# input: X: n*d incomplete data matrix;
#        K: number of mixtures;
#        Mu: K*d matrix, each row corresponds to a mixture mean;
#        P: K*1 matrix, each entry corresponds to the weight for a mixture;
#        Var: K*1 matrix, each entry corresponds to the variance for a mixture;
# output: Xnew: n*d data matrix with unrevealed entries filled
def fillMatrix(X,K,Mu,P,Var):
    n,d = np.shape(X)
    Xnew = np.copy(X)
	
    #Write your code here
    #Calculate posterior
    (post,LL)=Estep_part2(X,K,Mu,P,Var)
    for x in range(n):
        for y in range(d):
            if (X[x][y]==0):
                Xnew[x][y]=np.dot(post[x],Mu[:,y])
    return Xnew
#---------------------------------------------------------------------------------
