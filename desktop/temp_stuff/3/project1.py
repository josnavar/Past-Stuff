from string import punctuation, digits
from copy import deepcopy
import numpy as np
import matplotlib.pyplot as plt

### Parameters
T_perceptron = 5

T_avperceptron = 5

T_avgpa = 5
L_avgpa = 10
###

### Part I

def hinge_loss(feature_matrix, labels, theta, theta_0):
    """
    Section 1.2
    Finds the total hinge loss on a set of data given specific classification
    parameters.

    Args:
        feature_matrix - A numpy matrix describing the given data. Each row
            represents a single data point.
        labels - A numpy array where the kth element of the array is the
            correct classification of the kth row of the feature matrix.
        theta - A numpy array describing the linear classifier.
        theta_0 - A real valued number representing the offset parameter.


    Returns: A real number representing the hinge loss associated with the
    given dataset and parameters. This number should be the average hinge
    loss across all of the points in the feature matrix.
    """
    hinge_losses=[]
    dot_product=0
    final_hinge_loss=0
    for x in range(feature_matrix.shape[0]):
        for y in range(len(theta)):
            dot_product=dot_product+theta[y]*feature_matrix[x][y]
        dot_product=labels[x]*(dot_product+theta_0)
        if dot_product<=1:
            dot_product=1-dot_product
        else:
            dot_product=0
        hinge_losses.append(dot_product)
        dot_product=0
    for elt in hinge_losses:
        final_hinge_loss=final_hinge_loss+elt
    final_hinge_loss=1.0*final_hinge_loss/(len(theta))
    return final_hinge_loss
        
        
def perceptron_single_step_update(feature_vector, label, current_theta, current_theta_0):
    """
    Section 1.3
    Properly updates the classification parameter, theta and theta_0, on a
    single step of the perceptron algorithm.

    Args:
        feature_vector - A numpy array describing a single data point.
        label - The correct classification of the feature vector.
        current_theta - The current theta being used by the perceptron
            algorithm before this update.
        current_theta_0 - The current theta_0 being used by the perceptron
            algorithm before this update.

    Returns: A tuple where the first element is a numpy array with the value of
    theta after the current update has completed and the second element is a
    real valued number with the value of theta_0 after the current updated has
    completed.
    """
    final_theta_0=0
    result=()
    final_theta=[]
    dot_product=0
    dot_product=label*(np.inner(feature_vector,current_theta)+current_theta_0)
    if dot_product>0:
        result=(current_theta,current_theta_0)
        return result
    else:
        final_theta=current_theta+label*feature_vector
        final_theta_0=current_theta_0+label
        result=(final_theta,current_theta_0+label)
        return result
            

def perceptron(feature_matrix, labels, T):
    """
    Section 1.4
    Runs the full perceptron algorithm on a given set of data. Runs T
    iterations through the data set, there is no need to worry about
    stopping early.

    NOTE: Please use the previously implemented functions when applicable.
    Do not copy paste code from previous parts.

    Args:
        feature_matrix -  A numpy matrix describing the given data. Each row
            represents a single data point.
        labels - A numpy array where the kth element of the array is the
            correct classification of the kth row of the feature matrix.
        T - An integer indicating how many times the perceptron algorithm
            should iterate through the feature matrix.

    Returns: A tuple where the first element is a numpy array with the value of
    theta, the linear classification parameter, after T iterations through the
    feature matrix and the second element is a real number with the value of
    theta_0, the offset classification parameter, after T iterations through
    the feature matrix.
    """
    current_theta=[]
    current_theta_0=0
    for x in range(feature_matrix.shape[1]):
        current_theta.append(0)
    current_theta=np.asarray(current_theta)
    for x in range(T):
        for y in range(feature_matrix.shape[0]):
            temp=perceptron_single_step_update(feature_matrix[y],labels[y],current_theta,current_theta_0)
            current_theta=temp[0]
            current_theta_0=temp[1]
    return (current_theta,current_theta_0)
            
        

def passive_aggressive_single_step_update(feature_vector, label, L, current_theta, current_theta_0):
    """
    Section 1.5
    Properly updates the classification parameter, theta and theta_0, on a
    single step of the passive-aggressive algorithm

    Args:
        feature_vector - A numpy array describing a single data point.
        label - The correct classification of the feature vector.
        L - The lamba value being used to update the passive-aggressive
            algorithm parameters.
        current_theta - The current theta being used by the passive-aggressive
            algorithm before this update.
        current_theta_0 - The current theta_0 being used by the
            passive-aggressive algorithm before this update.

    Returns: A tuple where the first element is a numpy array with the value of
    theta after the current update has completed and the second element is a
    real valued number with the value of theta_0 after the current updated has
    completed.
    """
    #First calculate eta, by calculating loss function.
    x_prime=deepcopy(feature_vector)
    x_temp=np.asarray([current_theta_0])
    x_prime=np.append(x_prime,x_temp)

    loss=0
    dot_product=0
    dot_product=1.0-label*(np.inner(feature_vector,current_theta)+current_theta_0)
    
    loss=max((dot_product)/(np.linalg.norm(x_prime)**2),0)

    eta=min(loss,(1.0/L))

    final_theta_0=current_theta_0+label*eta
    final_theta=current_theta+(label*eta)*(feature_vector)
    return (final_theta,final_theta_0)
    

def average_perceptron(feature_matrix, labels, T):
    """
    Section 1.6
    Runs the average perceptron algorithm on a given set of data. Runs T
    iterations through the data set, there is no need to worry about
    stopping early.

    NOTE: Please use the previously implemented functions when applicable.
    Do not copy paste code from previous parts.

    Args:
        feature_matrix -  A numpy matrix describing the given data. Each row
            represents a single data point.
        labels - A numpy array where the kth element of the array is the
            correct classification of the kth row of the feature matrix.
        T - An integer indicating how many times the perceptron algorithm
            should iterate through the feature matrix.

    Returns: A tuple where the first element is a numpy array with the value of
    the average theta, the linear classification parameter, found after T
    iterations through the feature matrix and the second element is a real
    number with the value of the average theta_0, the offset classification
    parameter, found after T iterations through the feature matrix.

    Hint: It is difficult to keep a running average; however, it is simple to
    find a sum and divide.
    """
    final_theta=[]
    final_theta_0=0
    current_theta=[]
    current_theta_0=0
    for x in range(feature_matrix.shape[1]):
        current_theta.append(0)
        final_theta.append(0)
    current_theta=np.asarray(current_theta)
    final_theta=np.asarray(final_theta)
    for x in range(T):
        for y in range(feature_matrix.shape[0]):
            temp=perceptron_single_step_update(feature_matrix[y],labels[y],current_theta,current_theta_0)
            current_theta=temp[0]
            current_theta_0=temp[1]
            final_theta=final_theta+temp[0]
            final_theta_0=final_theta_0+temp[1]
    return ((final_theta*1.0)/(T*feature_matrix.shape[0]),(final_theta_0*1.0)/(T*feature_matrix.shape[0]))

def average_passive_aggressive(feature_matrix, labels, T, L):
    """
    Section 1.6
    Runs the average passive-agressive algorithm on a given set of data. Runs T
    iterations through the data set, there is no need to worry about
    stopping early.

    NOTE: Please use the previously implemented functions when applicable.
    Do not copy paste code from previous parts.

    Args:
        feature_matrix -  A numpy matrix describing the given data. Each row
            represents a single data point.
        labels - A numpy array where the kth element of the array is the
            correct classification of the kth row of the feature matrix.
        T - An integer indicating how many times the perceptron algorithm
            should iterate through the feature matrix.
        L - The lamba value being used to update the passive-agressive
            algorithm parameters.

    Returns: A tuple where the first element is a numpy array with the value of
    the average theta, the linear classification parameter, found after T
    iterations through the feature matrix and the second element is a real
    number with the value of the average theta_0, the offset classification
    parameter, found after T iterations through the feature matrix.

    Hint: It is difficult to keep a running average; however, it is simple to
    find a sum and divide.
    """
    final_theta=[]
    final_theta_0=0
    current_theta=[]
    current_theta_0=0
    for x in range(feature_matrix.shape[1]):
        current_theta.append(0)
        final_theta.append(0)
    current_theta=np.asarray(current_theta)
    final_theta=np.asarray(final_theta)
    for x in range(T):
        for y in range(feature_matrix.shape[0]):
            temp=passive_aggressive_single_step_update(feature_matrix[y],labels[y],L,current_theta,current_theta_0)
            current_theta=temp[0]
            current_theta_0=temp[1]
            final_theta=final_theta+temp[0]
            final_theta_0=final_theta_0+temp[1]
    return ((final_theta*1.0)/(T*feature_matrix.shape[0]),(final_theta_0*1.0)/(T*feature_matrix.shape[0]))

### Part II

def classify(feature_matrix, theta, theta_0):
    """
    Section 2.8
    A classification function that uses theta and theta_0 to classify a set of
    data points.

    Args:
        feature_matrix - A numpy matrix describing the given data. Each row
            represents a single data point.
                theta - A numpy array describing the linear classifier.
        theta - A numpy array describing the linear classifier.
        theta_0 - A real valued number representing the offset parameter.

    Returns: A numpy array of 1s and -1s where the kth element of the array is the predicted
    classification of the kth row of the feature matrix using the given theta
    and theta_0.
    """
    
    result=[]
    for elt in range(feature_matrix.shape[0]):
        if np.inner(feature_matrix[elt],theta)+theta_0>0:
            result.append(1)
        else:
            result.append(-1)
    result=np.asarray(result)
    return result
        

def perceptron_accuracy(train_feature_matrix, val_feature_matrix, train_labels, val_labels, T):
    """
    Section 2.9
    Trains a linear classifier using the perceptron algorithm with a given T
    value. The classifier is trained on the train data. The classifier's
    accuracy on the train and validation data is then returned.

    Args:
        train_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        val_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        train_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the training
            feature matrix.
        val_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the validation
            feature matrix.
        T - The value of T to use for training with the perceptron algorithm.

    Returns: A tuple in which the first element is the (scalar) accuracy of the
    trained classifier on the training data and the second element is the accuracy
    of the trained classifier on the validation data.
    """
    training_temp=perceptron(train_feature_matrix,train_labels,T)
    training_theta=training_temp[0]
    training_theta_0=training_temp[1]
    training_result=classify(train_feature_matrix,training_theta,training_theta_0)

    validation_result=classify(val_feature_matrix,training_theta,training_theta_0)
    return (accuracy(training_result,train_labels),accuracy(validation_result,val_labels))

def average_perceptron_accuracy(train_feature_matrix, val_feature_matrix, train_labels, val_labels, T):
    """
    Section 2.9
    Trains a linear classifier using the average perceptron algorithm with
    a given T value. The classifier is trained on the train data. The
    classifier's accuracy on the train and validation data is then returned.

    Args:
        train_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        val_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        train_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the training
            feature matrix.
        val_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the validation
            feature matrix.
        T - The value of T to use for training with the average perceptron
            algorithm.

    Returns: A tuple in which the first element is the (scalar) accuracy of the
    trained classifier on the training data and the second element is the accuracy
    of the trained classifier on the validation data.
    """
    training_temp=average_perceptron(train_feature_matrix,train_labels,T)
    training_theta=training_temp[0]
    training_theta_0=training_temp[1]
    training_result=classify(train_feature_matrix,training_theta,training_theta_0)

    validation_result=classify(val_feature_matrix,training_theta,training_theta_0)
    return (accuracy(training_result,train_labels),accuracy(validation_result,val_labels))

def average_passive_aggressive_accuracy(train_feature_matrix, val_feature_matrix, train_labels, val_labels, T, L):
    """
    Section 2.9
    Trains a linear classifier using the average passive aggressive algorithm
    with given T and L values. The classifier is trained on the train data.
    The classifier's accuracy on the train and validation data is then
    returned.

    Args:
        train_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        val_feature_matrix - A numpy matrix describing the training
            data. Each row represents a single data point.
        train_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the training
            feature matrix.
        val_labels - A numpy array where the kth element of the array
            is the correct classification of the kth row of the validation
            feature matrix.
        T - The value of T to use for training with the average passive
            aggressive algorithm.
        L - The value of L to use for training with the average passive
            aggressive algorithm.

    Returns: A tuple in which the first element is the (scalar) accuracy of the
    trained classifier on the training data and the second element is the accuracy
    of the trained classifier on the validation data.
    """
    training_temp=average_passive_aggressive(train_feature_matrix,train_labels,T,L)
    training_theta=training_temp[0]
    training_theta_0=training_temp[1]
    training_result=classify(train_feature_matrix,training_theta,training_theta_0)

    validation_result=classify(val_feature_matrix,training_theta,training_theta_0)
    return (accuracy(training_result,train_labels),accuracy(validation_result,val_labels))

def extract_words(input_string):
    """
    Helper function for bag_of_words()
    Inputs a text string
    Returns a list of lowercase words in the string.
    Punctuation and digits are separated out into their own words.
    """
    for c in punctuation + digits:
        input_string = input_string.replace(c, ' ' + c + ' ')

    return input_string.lower().split()

def bag_of_words(texts):
    """
    Inputs a list of string reviews
    Returns a dictionary of unique unigrams occurring over the input

    Feel free to change this code as guided by Section 3 (e.g. remove stopwords, add bigrams etc.)
    """
    stop="imemymyselfweouroursourselvesyouyouryoursyourselfyourselveshehimhishimselfsheherhersherselfititsitselftheythemtheirtheirsthemselveswhatwhichwhowhomthisthatthesethoseamisarewaswerebebeenbeinghavehashadhavingdodoesdiddoingaantheandbutiforbecauseasuntilwhileofatbyforwithaboutagainstbetweenintothroughduringbeforeafterabovebelowtofromupdowninoutonoffoverunderagainfurtherthenonceheretherewhenwherewhyhowallanybotheachfewmoremostothersomesuchnonornotonlyownsamesothantooverystcanwilljustdonshouldnow"
    dictionary = {} # maps word to unique index
    for text in texts:
        word_list = extract_words(text)
        for word in word_list:
            if word not in dictionary and word not in stop:
                dictionary[word] = len(dictionary)
    return dictionary

def extract_bow_feature_vectors(reviews, dictionary):
    """
    Inputs a list of string reviews
    Inputs the dictionary of words as given by bag_of_words
    Returns the bag-of-words feature matrix representation of the data.
    The returned matrix is of shape (n, m), where n is the number of reviews
    and m the total number of entries in the dictionary.
    """

    num_reviews = len(reviews)
    feature_matrix = np.zeros([num_reviews, len(dictionary)])

    for i, text in enumerate(reviews):
        word_list = extract_words(text)
        for word in word_list:
            if word in dictionary:
                feature_matrix[i, dictionary[word]] = 1
    return feature_matrix

def extract_additional_features(reviews):
    """
    Section 3.12
    Inputs a list of string reviews
    Returns a feature matrix of (n,m), where n is the number of reviews
    and m is the total number of additional features of your choice

    YOU MAY CHANGE THE PARAMETERS
    """
    return np.ndarray((len(reviews), 0))

def extract_final_features(reviews, dictionary):
    """
    Section 3.12
    Constructs a final feature matrix using the improved bag-of-words and/or additional features
    """
    bow_feature_matrix = extract_bow_feature_vectors(reviews,dictionary)
    additional_feature_matrix = extract_additional_features(reviews)
    return np.hstack((bow_feature_matrix, additional_feature_matrix))

def accuracy(preds, targets):
    """
    Given length-N vectors containing predicted and target labels,
    returns the percentage and number of correct predictions.
    """
    return (preds == targets).mean()

