#include <jni.h>

#ifndef _Included_com_example_kanjiapplication_DrawFragment
#define _Included_com_example_kanjiapplication_DrawFragment
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_kanjiapplication_MainActivity
 * Method:    normalization
 * Signature: ()V
 */
JNIEXPORT jobjectArray JNICALL Java_com_example_kanjiapplication_DrawFragment_normalization
    (JNIEnv *env, jobject obj, jobjectArray image);

#ifdef __cplusplus
}
#endif
#endif
