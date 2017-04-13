package com.jim.classificationv21;


import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import smile.classification.KNN;
import smile.classification.LDA;
import smile.classification.SVM;
import smile.classification.NeuralNetwork;
import smile.math.kernel.GaussianKernel;
import smile.math.kernel.LinearKernel;
import smile.math.kernel.PolynomialKernel;
import smile.validation.CrossValidation;
import smile.validation.LOOCV;
import smile.math.Math;

public class MachineLearningClass {
    TrainingAndClassifyingActivity parent;

    ArrayList trainingData_ArrayList;
    ArrayList trainingDataGesture_ArrayList;
    KNN knn;
    SVM svm;
    LDA lda;
    NeuralNetwork neuralNetwork;

    MachineLearningClass(TrainingAndClassifyingActivity parent) {
        this.parent = parent;
        trainingData_ArrayList = new ArrayList();
        trainingDataGesture_ArrayList = new ArrayList();
    }

    void addTrainingData(ArrayList data) {
        trainingData_ArrayList.add(data);
        trainingDataGesture_ArrayList.add(parent.currentNumberOfGestures % MainActivity.numberGestures);

        parent.trainingFeedbackText.setText("Please Hold Current Gesture \nSamples Received: " + trainingData_ArrayList.size() % MainActivity.trainingSamples + "/" + MainActivity.trainingSamples);

        //Log.i("addTrainingData", "trainingData_ArrayList.size =" + trainingData_ArrayList.size() );
        if (trainingData_ArrayList.size() == (parent.currentNumberOfGestures + 1) * MainActivity.trainingSamples)
        {
            parent.trainingFeedbackText.setText("All Samples Received");
            parent.trainingComplete();
        }
    }

    String trainModels() {

        Log.i("trainModels", "trainingData_ArrayList.size =" + trainingData_ArrayList.size());
        String result = "";

        int trainingDataLength = trainingData_ArrayList.size();
        //===============================================
        // Training
        double[][] x = new double[trainingDataLength][10];
        int[] y = new int[trainingDataLength];

        for (int i = 0; i < trainingDataLength; i++)
        {
            y[i] = (int) trainingDataGesture_ArrayList.get(i);

            Log.i("trainModels", "label: " + y[i]);
            ArrayList data = (ArrayList) trainingData_ArrayList.get(i);
            for (int j = 0; j < 10; j++)
            {
                x[i][j] = (int) data.get(j);
            }
        }

        /*
        try
        {
        }
        catch (Exception e)
        {
            Log.v("MachineLearningClass", "Error training models: " + e);
            result += "Error training models: " + e;
        }
        */
        knn = KNN.learn(x, y);

        lda = new LDA(x, y);

        // val svm = new SVM[Array[Double]](new GaussianKernel(0.01), 1.0,2)
        svm = new SVM(new PolynomialKernel(10), 5, MainActivity.numberGestures, SVM.Multiclass.ONE_VS_ONE);

        //svm.setTolerance(10);
        //svm = new SVM<>(new GaussianKernel(8), 5);
        svm.learn(x, y);
        svm.learn(x, y);
        svm.learn(x, y);
        svm.learn(x, y);
        svm.learn(x, y);
        svm.finish();

        //Neuraln Net
        // just put in five for num units?
        neuralNetwork = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.SOFTMAX, 10, MainActivity.numberGestures);
        neuralNetwork.learn(x, y);
        neuralNetwork.learn(x, y);
        neuralNetwork.learn(x, y);
        neuralNetwork.learn(x, y);
        neuralNetwork.learn(x, y);


        String errorRateKNN = "\nKNN";
        String errorRateLDA = "\nLDA";
        String errorRateSVM = "\nSVM";
        String errorRateNN = "\nNeural Network";

/*
        //===============================================
        // Error

        //validate model
        //run through training data and see if it classifies properly testing

        int errorKNN = 0;
        int errorLDA = 0;
        int errorSVM = 0;
        int errorNN = 0;


        for (int i = 0; i < trainingDataLength; i++)
        {

            if (knn.predict(x[i]) != y[i]) {
                errorKNN++;
            }
            if (lda.predict(x[i]) != y[i]) {
                errorLDA++;
            }

            //Log.i("trainModels","x[i] svm.predict(x[i]) != y[i] :: " + svm.predict(x[i]) + " != "+ y[i] );
            if (svm.predict(x[i]) != y[i]) {
                errorSVM++;
            }
            if (neuralNetwork.predict(x[i]) != y[i]) {
                errorNN++;
            }

        }

        errorRateKNN += " error rate = " + (int) (100.0 * errorKNN / trainingDataLength) + "%,";
        errorRateLDA += " error rate = " + (int) (100.0 * errorLDA / trainingDataLength) + "%,";
        errorRateSVM += " error rate = " + (int) (100.0 * errorSVM / trainingDataLength) + "%,";
        errorRateNN += " error rate = " + (int) (100.0 * errorNN / trainingDataLength) + "%,";

        Log.i("trainModels", "# of errors: " + errorKNN +"," + errorLDA+","+ errorSVM+","+errorNN );


        //===============================================
        // LOOCV

        int loocvErrorKNN = 0;
        int loocvErrorLDA = 0;
        int loocvErrorSVM = 0;
        int loocvErrorNN = 0;

        double[][] loocvX = new double[trainingDataLength -1][10];
        int[] loocvY = new int[trainingDataLength -1];

        for (int i = 0; i < trainingDataLength; i++)
        {

            for (int j = 0; j < trainingDataLength - 1; j++)
            {
                if( j != i )
                {
                    int index;
                    if( j < i )
                    {
                        index = j;
                    }
                    else
                    {
                        index = j-1;
                    }

                    ArrayList data = (ArrayList) trainingData_ArrayList.get(j);
                    for (int m = 0; m < 10; m++)
                    {
                        loocvX[index][m] = (int) data.get(m);
                    }

                    loocvY[index] = (int) trainingDataGesture_ArrayList.get(index);

                }
            }

            KNN loocvKNN = KNN.learn(loocvX, loocvY);
            LDA loocvLDA = new LDA(loocvX, loocvY);

            SVM loocvSVM = new SVM(new PolynomialKernel(10), 5, MainActivity.numberGestures, SVM.Multiclass.ONE_VS_ONE);

            loocvSVM.learn(loocvX, loocvY);
            loocvSVM.learn(loocvX, loocvY);
            loocvSVM.learn(loocvX, loocvY);
            loocvSVM.learn(loocvX, loocvY);
            loocvSVM.learn(loocvX, loocvY);
            loocvSVM.finish();

            NeuralNetwork loocvNeuralNetwork = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.SOFTMAX, 10, MainActivity.numberGestures);
            loocvNeuralNetwork.learn(loocvX, loocvY);
            loocvNeuralNetwork.learn(loocvX, loocvY);
            loocvNeuralNetwork.learn(loocvX, loocvY);
            loocvNeuralNetwork.learn(loocvX, loocvY);
            loocvNeuralNetwork.learn(loocvX, loocvY);


            if (loocvKNN.predict(x[i]) != y[i]) {
                loocvErrorKNN++;
            }
            if (loocvLDA.predict(x[i]) != y[i]) {
                loocvErrorLDA++;
            }
            if (loocvSVM.predict(x[i]) != y[i]) {
                loocvErrorSVM++;
            }
            if (loocvNeuralNetwork.predict(x[i]) != y[i]) {
                loocvErrorNN++;
            }
        }

        errorRateKNN += " LOOCV = " + (int) (100.0 * loocvErrorKNN / trainingDataLength) + "%,";
        errorRateLDA += " LOOCV = " + (int) (100.0 * loocvErrorLDA / trainingDataLength) + "%,";
        errorRateSVM += " LOOCV = " + (int) (100.0 * loocvErrorSVM / trainingDataLength) + "%,";
        errorRateNN  += " LOOCV = " + (int) (100.0 * loocvErrorNN / trainingDataLength) + "%,";


        //===============================================
        // SMILE loocv

        int smileLoocvErrorKNN = 0;
        int smileLoocvErrorLDA = 0;
        int smileLoocvErrorSVM = 0;
        int smileLoocvErrorNN = 0;

        LOOCV loocv = new LOOCV(trainingDataLength);

        for (int i = 0; i < trainingDataLength; i++)
        {
            double[][] trainX = Math.slice(x, loocv.train[i]);
            int[] trainY = Math.slice(y, loocv.train[i]);

            KNN smileLoocvKNN = KNN.learn(trainX, trainY);
            LDA smileLoocvLDA = new LDA(trainX, trainY);

            SVM smileLoocvSVM = new SVM(new PolynomialKernel(10), 5,MainActivity.numberGestures, SVM.Multiclass.ONE_VS_ONE);

            smileLoocvSVM.learn(trainX, trainY);
            smileLoocvSVM.learn(trainX, trainY);
            smileLoocvSVM.learn(trainX, trainY);
            smileLoocvSVM.learn(trainX, trainY);
            smileLoocvSVM.learn(trainX, trainY);
            smileLoocvSVM.finish();

            NeuralNetwork smileLoocvNeuralNetwork = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.SOFTMAX, 10, MainActivity.numberGestures);
            smileLoocvNeuralNetwork.learn(trainX, trainY);
            smileLoocvNeuralNetwork.learn(trainX, trainY);
            smileLoocvNeuralNetwork.learn(trainX, trainY);
            smileLoocvNeuralNetwork.learn(trainX, trainY);
            smileLoocvNeuralNetwork.learn(trainX, trainY);


            if (smileLoocvKNN.predict(x[loocv.test[i]])!= y[loocv.test[i]]) {
                smileLoocvErrorKNN++;
            }
            if (smileLoocvLDA.predict(x[loocv.test[i]])!= y[loocv.test[i]]) {
                smileLoocvErrorLDA++;
            }
            if (smileLoocvSVM.predict(x[loocv.test[i]])!= y[loocv.test[i]]) {
                smileLoocvErrorSVM++;
            }
            if (smileLoocvNeuralNetwork.predict(x[loocv.test[i]])!= y[loocv.test[i]]) {
                smileLoocvErrorNN++;
            }
        }

        errorRateKNN += " LOOCV = " + (int) (100.0 * smileLoocvErrorKNN / trainingDataLength) + "%,";
        errorRateLDA += " LOOCV = " + (int) (100.0 * smileLoocvErrorLDA / trainingDataLength) + "%,";
        errorRateSVM += " LOOCV = " + (int) (100.0 * smileLoocvErrorSVM / trainingDataLength) + "%,";
        errorRateNN  += " LOOCV = " + (int) (100.0 * smileLoocvErrorNN / trainingDataLength) + "%,";


        //===============================================
        // k-fold cross validation

        int kFoldErrorKNN = 0;
        int kFoldErrorLDA = 0;
        int kFoldErrorSVM = 0;
        int kFoldErrorNN = 0;

        CrossValidation crossValidation = new CrossValidation(trainingDataLength, 10);

        for (int i = 0; i < 10; i++) {
            double[][] trainX = Math.slice(x, crossValidation.train[i]);
            int[] trainY = Math.slice(y, crossValidation.train[i]);

            KNN kFoldKNN = KNN.learn(trainX, trainY);
            LDA kFoldLDA = new LDA(trainX, trainY);

            SVM kFoldSVM = new SVM(new PolynomialKernel(10), 5, MainActivity.numberGestures, SVM.Multiclass.ONE_VS_ONE);

            kFoldSVM.learn(trainX, trainY);
            kFoldSVM.learn(trainX, trainY);
            kFoldSVM.learn(trainX, trainY);
            kFoldSVM.learn(trainX, trainY);
            kFoldSVM.learn(trainX, trainY);
            kFoldSVM.finish();

            NeuralNetwork kFoldNeuralNetwork = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.SOFTMAX, 10, MainActivity.numberGestures);
            kFoldNeuralNetwork.learn(trainX, trainY);
            kFoldNeuralNetwork.learn(trainX, trainY);
            kFoldNeuralNetwork.learn(trainX, trainY);
            kFoldNeuralNetwork.learn(trainX, trainY);
            kFoldNeuralNetwork.learn(trainX, trainY);


            for (int j = 0; j < crossValidation.test[i].length; j++) {
                if (kFoldKNN.predict(x[crossValidation.test[i][j]]) != y[crossValidation.test[i][j]]) {
                    kFoldErrorKNN++;
                }
                if (kFoldLDA.predict(x[crossValidation.test[i][j]]) != y[crossValidation.test[i][j]]) {
                    kFoldErrorLDA++;
                }
                if (kFoldSVM.predict(x[crossValidation.test[i][j]]) != y[crossValidation.test[i][j]]) {
                    kFoldErrorSVM++;
                }
                if (kFoldNeuralNetwork.predict(x[crossValidation.test[i][j]]) != y[crossValidation.test[i][j]]) {
                    kFoldErrorNN++;
                }
            }

        }

        errorRateKNN += " 10-Fold = " + (int) (100.0 * kFoldErrorKNN / trainingDataLength) + "%,";
        errorRateLDA += " 10-Fold = " + (int) (100.0 * kFoldErrorLDA / trainingDataLength) + "%,";
        errorRateSVM += " 10-Fold = " + (int) (100.0 * kFoldErrorSVM / trainingDataLength) + "%,";
        errorRateNN += " 10-Fold = " + (int) (100.0 * kFoldErrorNN / trainingDataLength) + "%,";

        Log.i("trainModels", "# of 10-fold errors: " + kFoldErrorKNN +"," + kFoldErrorLDA+","+ kFoldErrorSVM+","+kFoldErrorNN );
*/
        //===============================================
        // Result


        result += errorRateKNN;
        result += errorRateLDA;
        result += errorRateSVM;
        result += errorRateNN;


        Log.i("trainModels", "result = " + result);

        return result;

    }


    String classifyData(ArrayList data) {

        //double[][] x = new double[1][10];
        //double[] y = new double[data.size()];

        double[] z = new double[10];

        for (int i = 0; i < z.length; i++) {
            z[i] = (int) data.get(i);
        }

        int knnResult = knn.predict(z);
        int ldaResult = lda.predict(z);

        int svmResult = svm.predict(z);

        int nnResult = neuralNetwork.predict(z);

        //Log.i("trainModels", "Classifying input: " +  Arrays.toString(x[0]));

        String result = "";
        result += "\nKNN result = " + (knnResult + 1);
        result += "\nLDA result = " + (ldaResult + 1);
        result += "\nSVM result = " + (svmResult + 1);
        result += "\nNeural Network result = " + (nnResult + 1);

        parent.classificationFeedbackTextLarge.setText("" + (ldaResult + 1));


        return "Classification results" + result;
    }
}