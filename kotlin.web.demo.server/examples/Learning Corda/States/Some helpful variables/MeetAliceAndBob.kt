
/**
 * Throughout these lessons, we'll be needing some helpful variables to assist us in running our code.
 * Let's introduce those here.
*/
package education

import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.finance.*
import net.corda.testing.ALICE
import net.corda.testing.BOB
import org.junit.Test
import java.util.Currency

fun main(args: Array<String>) {

// ALICE and BOB are actually Corda [Party] objects.
// Whenever you want to interact with another network participant, you'll using this datatype.
// But for testing purposes, we've provided a few pre-made parties for you.

    println("Hi, I'm ${ALICE.name.organisation}")
    println("And I'm ${BOB.name.organisation}")

// We're not going to delve too deeply into what exactly is meaning of £100 or $100 in a distributed ledger
// but for getting started, you can use these helpful kotlin extensions to create something that represents cash

    one_hundred_pounds = 100.POUNDS
    two_hundred_dollars = 200.DOLLARS

    println("Some sample built in currencies: $one_hundred_pounds or $two_hundred_dollars")

// There are other ways to create these as well...
// Important note (that I guarantee you'll forget - I know I always do).
// --> When using this constructor, we denominate to the cent / penny

    val five_pounds = Amount(500, GBP)
    val one_dollar = Amount(100, USD)
    val two_dollars = Amount(200, USD)

// Obviously, you can't add mismatching currencies (uncomment the line below and run this to try if you don't believe me).
// println(five_pounds + one_dollar)

// But adding the same currency type is possible
    println(one_dollars + two_dollars)

}


