package jp.speakbuddy.edisonandroidexercise.util

import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import jp.speakbuddy.edisonandroidexercise.model.Fact
import java.security.MessageDigest

class CatsMapper {

    /** Mapper to convert Fact data class to CatFactLocal (Local Room)
     * Typically you can can put this in a use-case/util class if it gets any more complicated,
     * But since this app is fairly simple, I will leave it here for less complexity.
     * I also opted not to clone the Fact object from the API response with the Room DB, in my
     * experience usually local has more added data for management, and it's easier on handling
     * updates(usually)
     **/
    fun mapFactToLocal(fact: Fact): CatFactLocal {
        return CatFactLocal(
            factHashKey = generateHashFact(fact.fact),
            fact = fact.fact,
            length = fact.length,
            isFavourite = fact.isFavourite
        )
    }

    fun mapLocalToFact(fact: CatFactLocal): Fact {
        return Fact(
            fact = fact.fact,
            length = fact.length,
            isFavourite = fact.isFavourite
        )
    }

    /**
     * I was thinking of how to compare Facts with one another without comparing strings,
     * functionally I can just use something simple since it's just a cat fact, but IF I had to
     * think about scalability or "what's the proper way to do it", I wanted something that actually
     * feels right. I've considered unique IDs, partial compare, and some other common methods but
     * eventually I opted for cryptographic hashing. I've used a bunch of these back when I was
     * working on a blockchain project. Since SHA-256 hashing output is always a 32 byte string
     * output regardless of the content's length, it would essentially be easier to manage from
     * a human-eye perspective as well. Worse case you could string compare the hash key rather
     * than a unspecified X-length cat fact string, where the duration of check would be more
     * consistent due to the length of every hash.
     *
     * I've also taken into consideration CPU cycles(although modern ARM chips in phones don't have
     * a lack of CPU IPCs compared to 2018) for hashing vs string compare, technically the
     * difference is negligible but it's something I am always curious about for efficiency sake.
     *
     * This can also maaaaybe be considered as a usecase for a potential "backup" of the content if
     * ever the data were to be lost but the hash output remains(better if you hashed the entire
     * CatFact object but in this case I only hashed the cat fact string), but i've implemented no
     * such function for that here.
     *
     * Sorry for the long story, I may have overthought this process but I wanted to give the reason
     * of why I chose this.
     */
    fun generateHashFact(fact: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(fact.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

}