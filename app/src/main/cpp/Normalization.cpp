//
// Created by My_machine on 04.06.2023.
//

#include "Normalization.h"
#include<iostream>
#include<fstream>
#include<string>
#include<cmath>

const int NeedHeight = 64, NeedWidth = 64;
float** HighlightedData;
int HighlightedHeight, HighlightedWidth;
float** NormalSizeData;

void RemoveEmptySpaces()
{
	const float percent = 0.15;
	int percentHeight = (int)ceil((float)HighlightedHeight * percent);
	int percentWidth = (int)ceil((float)HighlightedWidth * percent);
	int countEmpty = 0;
	bool isFull = false;
	bool needChange = false;
	int changeHeight = 0;
	int changeWidth = 0;
	for (int i = 0; i < HighlightedHeight; i++)
	{
		for (int j = 0; j < HighlightedWidth; j++)
			if (HighlightedData[i][j] > 0.0)
			{
				isFull = true;
				break;
			}
		if (isFull)
			countEmpty = 0;
		else
			countEmpty++;
		if (countEmpty > percentHeight)
		{
			for (int j = 0; j < HighlightedWidth; j++)
				HighlightedData[i][j] = -1.0;
			needChange = true;
			changeHeight++;
		}
		isFull = false;
	}
	countEmpty = 0;
	for (int j = 0; j < HighlightedWidth; j++)
	{
		for (int i = 0; i < HighlightedHeight; i++)
			if (HighlightedData[i][j] > 0.0)
			{
				isFull = true;
				break;
			}
		if (isFull)
			countEmpty = 0;
		else
			countEmpty++;
		if (countEmpty > percentWidth)
		{
			for (int i = 0; i < HighlightedHeight; i++)
				HighlightedData[i][j] = -1.0;
			needChange = true;
			changeWidth++;
		}
		isFull = false;
	}
	if (needChange)
	{
		float** copyHighlightedData = new float* [HighlightedHeight];
		for (int i = 0; i < HighlightedHeight; i++)
		{
			copyHighlightedData[i] = new float[HighlightedWidth];
			for (int j = 0; j < HighlightedWidth; j++)
				copyHighlightedData[i][j] = HighlightedData[i][j];
		}
		HighlightedData = new float* [HighlightedHeight - changeHeight];
		for (int i = 0; i < HighlightedHeight - changeHeight; i++)
			HighlightedData[i] = new float [HighlightedWidth - changeWidth];
		int iH = 0, iW = 0;
		isFull = false;
		for (int i = 0; i < HighlightedHeight; i++)
		{
			for (int j = 0; j < HighlightedWidth; j++)
			{
				if (copyHighlightedData[i][j] != -1.0)
				{
					HighlightedData[iH][iW] = copyHighlightedData[i][j];
					iW++;
					isFull = true;
				}
			}
			if (isFull)
				iH++;
			iW = 0;
			isFull = false;
		}
		HighlightedHeight -= changeHeight;
		HighlightedWidth -= changeWidth;
		delete[] copyHighlightedData;
	}
}

void ExpandAndShrinkScaling()
{
	float** extendedArray = new float* [HighlightedHeight * NeedHeight];
	for (int i = 0; i < HighlightedHeight * NeedHeight; i++)
		extendedArray[i] = new float[HighlightedWidth * NeedWidth];
	for (int i = 0; i < HighlightedHeight; i++)
		for (int j = 0; j < HighlightedWidth; j++)
			for (int k = 0; k < NeedHeight; k++)
				for (int g = 0; g < NeedWidth; g++)
					extendedArray[i * NeedHeight + k][j * NeedWidth + g] = HighlightedData[i][j];
	NormalSizeData = new float* [NeedHeight];
	for (int i = 0; i < NeedHeight; i++)
	{
		NormalSizeData[i] = new float[NeedWidth];
		for (int j = 0; j < NeedWidth; j++)
			NormalSizeData[i][j] = 0.0;
	}
	for (int i = 0; i < NeedHeight; i++)
		for (int j = 0; j < NeedWidth; j++) {
			for (int k1 = 0; k1 < HighlightedHeight; k1++)
				for (int k2 = 0; k2 < HighlightedWidth; k2++)
					NormalSizeData[i][j] += extendedArray[i * HighlightedHeight + k1][j * HighlightedWidth + k2];
			NormalSizeData[i][j] /= (double)HighlightedHeight * (double)HighlightedWidth;
		}
}

void ExpandWorH()
{
	bool isExpandHeight = false;
	float compRateF = (float)HighlightedHeight / (float)HighlightedWidth;
	if (compRateF < 1)
	{
		compRateF = (float)HighlightedWidth / (float)HighlightedHeight;
		isExpandHeight = true;
	}
	int compRateI = round(compRateF);
	if (compRateI == 1)
		return;
	float** copyHighlightedData = new float* [HighlightedHeight];
	for (int i = 0; i < HighlightedHeight; i++)
	{
		copyHighlightedData[i] = new float [HighlightedWidth];
		for (int j = 0; j < HighlightedWidth; j++)
			copyHighlightedData[i][j] = HighlightedData[i][j];
	}
	if (isExpandHeight)
	{
		HighlightedData = new float* [HighlightedHeight * compRateI];
		for (int i = 0; i < HighlightedHeight; i++)
			for (int k = 0; k < compRateI; k++)
			{
				HighlightedData[i * compRateI + k] = new float[HighlightedWidth];
				for (int j = 0; j < HighlightedWidth; j++)
					HighlightedData[i * compRateI + k][j] = copyHighlightedData[i][j];
			}
		HighlightedHeight *= compRateI;
	}
	else
	{
		HighlightedData = new float* [HighlightedHeight];
		for (int i = 0; i < HighlightedHeight; i++)
		{
			HighlightedData[i] = new float[HighlightedWidth * compRateI];
			for (int j = 0; j < HighlightedWidth; j++)
				for (int k = 0; k < compRateI; k++)
					HighlightedData[i][j * compRateI + k] = copyHighlightedData[i][j];
		}
		HighlightedWidth *= compRateI;
	}
	delete[] copyHighlightedData;
}

void FillOne(int maxCompHeight, float** array)
{
	for (int i = 0; i < maxCompHeight; i++)
		for (int j = 0; j < HighlightedWidth; j++)
			array[i][j] = 1.0;
}

void ShrinkScaling()
{
	NormalSizeData = new float* [NeedHeight];
	for (int i = 0; i < NeedHeight; i++)
	{
		NormalSizeData[i] = new float[NeedWidth];
		for (int j = 0; j < NeedWidth; j++)
			NormalSizeData[i][j] = 0.0;
	}
	float compressionHeight = (float)HighlightedHeight / (float)NeedHeight;
	compressionHeight *= 1000.0;
	compressionHeight = round(compressionHeight);
	compressionHeight /= 1000.0;
	int maxCompHeight = ceil(compressionHeight);
	int minCompHeight = floor(compressionHeight);
	float compressionWidth = (float)HighlightedWidth / (float)NeedWidth;
	compressionWidth *= 1000.0;
	compressionWidth = round(compressionWidth);
	compressionWidth /= 1000.0;
	int maxCompWidth = ceil(compressionWidth);
	int minCompWidth = floor(compressionWidth);
	float** gridLayoutH = new float* [HighlightedHeight];
	for (int i = 0; i < HighlightedHeight; i++)
	{
		gridLayoutH[i] = new float[HighlightedWidth];
		for (int j = 0; j < HighlightedWidth; j++)
			gridLayoutH[i][j] = 1.0;
	}
	float** gridLayoutW = new float* [maxCompHeight + 1];
	for (int i = 0; i < maxCompHeight + 1; i++)
		gridLayoutW[i] = new float[HighlightedWidth];
	FillOne(maxCompHeight + 1, gridLayoutW);
	float counterH = 0, counterW = 0;
	float compCellH = 0, compCellW = 0;
	int g, k;
	int lastH = 0, lastW = 0;
	for (int i = 0; i < NeedHeight; i++)
	{
		for (int j = 0; j < NeedWidth; j++)
		{
			counterH = compressionHeight;
			k = 0;
			while (counterH > 0)
			{
				counterW = compressionWidth;
				g = 0;
				while (counterW > 0)
				{
					if (counterH >= gridLayoutH[lastH + k][lastW + g])
						compCellH = gridLayoutH[lastH + k][lastW + g];
					else
					{
						compCellH = counterH;
						if (gridLayoutH[lastH + k][lastW + g] == 1)
							if (counterH <= 1)
								gridLayoutH[lastH + k][lastW + g] -= counterH;
							else
								gridLayoutH[lastH + k][lastW + g] -= 1;
					}

					if (counterW >= gridLayoutW[k][lastW + g])
					{
						compCellW = gridLayoutW[k][lastW + g];
						counterW -= gridLayoutW[k][lastW + g];
					}
					else
					{
						compCellW = counterW;
						if (gridLayoutW[k][lastW + g] == 1)
							if (counterW <= 1)
								gridLayoutW[k][lastW + g] -= counterW;
							else
								gridLayoutW[k][lastW + g] -= 1;
						counterW = 0.0;
					}
					if (lastW + g == HighlightedWidth - 1)
						counterW = 0;
					NormalSizeData[i][j] += HighlightedData[lastH + k][lastW + g] * compCellH * compCellW;
					g++;
				}
				if (k != maxCompHeight && lastH + k < HighlightedHeight - 1)
					counterH -= compCellH;
				else
					counterH = 0;
				k++;
			}
			NormalSizeData[i][j] /= (compressionHeight * compressionWidth);
			lastW = lastW + g - 1;
		}
		lastW = 0;
		FillOne(maxCompHeight + 1, gridLayoutW);
		lastH = lastH + k - 1;
	}
	delete[] gridLayoutH;
	delete[] gridLayoutW;
}

void ResettingMinimumAndMaximumValues()
{
	float compNumber = 0.35;
	for (int i = 0; i < NeedHeight; i++)
		for (int j = 0; j < NeedWidth; j++)
			if (NormalSizeData[i][j] < compNumber)
				NormalSizeData[i][j] = 0.0;
			else
				if (NormalSizeData[i][j] >= 0.94)
					NormalSizeData[i][j] = 1.0;
}

JNIEXPORT jobjectArray JNICALL Java_com_example_kanjiapplication_DrawFragment_normalization(JNIEnv *env, jobject obj, jobjectArray image)
{
    //Read java array to c++ array
    jarray jniarray;
    jint height = env->GetArrayLength(image);
    HighlightedHeight = height;
    HighlightedData = new float* [height];
    for (jint i = 0; i < height; i++)
    {
        jniarray = (jfloatArray)env->GetObjectArrayElement(image, i);
        jint width = env->GetArrayLength(jniarray);
        HighlightedWidth = width;
        jfloat*data = env->GetFloatArrayElements((jfloatArray)jniarray, nullptr);
        HighlightedData[i] = new float[width];
        for (jint j = 0; j < width; j++)
            HighlightedData[i][j] = data[j];
        env->ReleaseFloatArrayElements((jfloatArray)jniarray, data, 0);
    }
    //Normalization
    RemoveEmptySpaces();
    if (HighlightedHeight < 64 || HighlightedWidth < 64)
	    ExpandAndShrinkScaling();
    else
    {
	    ExpandWorH();
	    ShrinkScaling();
    }
    ResettingMinimumAndMaximumValues();
    //Return c++ array to java array
    jobjectArray jImage;
    jclass floatArr = env->FindClass("[F");
    jImage = env->NewObjectArray(NeedHeight * NeedWidth, floatArr, nullptr);
    jfloat temp[NeedWidth];
    for (jint i = 0; i < NeedHeight; i++){
        jfloatArray colArr = env->NewFloatArray(NeedWidth);
        for (jint j = 0; j < NeedWidth; j++)
            temp[j] = NormalSizeData[i][j];
        env->SetFloatArrayRegion(colArr, 0, NeedWidth, temp);
        env->SetObjectArrayElement(jImage, i, colArr);
        env->DeleteLocalRef(colArr);
    }
    return jImage;
}