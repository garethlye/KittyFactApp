package jp.speakbuddy.edisonandroidexercise.base

object CommonConstants {
    /**
     * Hard coded static strings are generally not a good practice, but for these types of strings
     * that should never be modified(even accidentally) personally shouldn't exist in strings.xml.
     * Even knowing this, by placing it in strings.xml you increase the probability of accidentally
     * modifying it. Especially when you deal with translations like I have here.
     *
     * Since these are part of core-functionalities, they need a safe place to stay.
     *
     * I did consider using BuildConfig, but since these are not separated by Build Flavors so not
     * the most sense in doing it. If these were sensitive like secret keys, then I would use
     * secret storage. I hope this is an alright reason for this implementation.
     */
    const val BASE_URL = "https://catfact.ninja/"

    const val CAT_FACT_LOCAL_DB_NAME = "cat_fact_database"

    const val CAT_MODEL_TENSORFLOWLITE = "cat_model.tflite"
    const val CAT_MODEL_TENSOR_LABELS = "labels.txt"

    const val CAT_MODEL_SHARE_IMG = "cat_fact_share.png"
}