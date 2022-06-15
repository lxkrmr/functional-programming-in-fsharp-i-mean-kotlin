// https://trustbit.tech/blog/2019/09/13/introduction-to-functional-programming-in-f-sharp-part-1

/*
Feature: Applying a discount
Scenario: Eligible Registered Customers get 10% discount when they spend £100 or more

Given the following Registered Customers
|Customer Id|Is Eligible|Is Registered|
|John       |true       |true         |
|Mary       |true       |true         |
|Richard    |false      |true         |
|Sarah      |false      |false        |

When <Customer Id> spends <Spend>
Then their order total will be <Total>

Examples:
|Customer Id|   Spend|   Total|
|Mary       |   99.00|   99.00|
|John       |  100.00|   90.00|
|Richard    |  100.00|  100.00|
|Sarah      |  100.00|  100.00|
 */

// v1: first approach
data class CustomerV1(val id: String, val isEligible: Boolean, val isRegistered: Boolean)

fun calculateTotalV1(customerV1: CustomerV1, spend: Double): Double {
    val discount = if (customerV1.isRegistered && customerV1.isEligible && spend >= 100) spend * 0.1 else 0.0
    return spend - discount
}
// fyi: we can't omit the types as in f# in our method signature

// v2: Making the implicit explicit

sealed interface CustomerV2
data class RegisteredCustomerV2(val id: String, val isEligible: Boolean) : CustomerV2
data class UnregisteredCustomerV2(val id: String) : CustomerV2

fun calculateTotalV2(customerV2: CustomerV2, spend: Double): Double {
    val discount = when(customerV2) {
        is RegisteredCustomerV2 -> if (customerV2.isEligible && (spend >= 100.0)) spend * 0.1 else 0.0
        is UnregisteredCustomerV2 -> 0.0
    }

    return spend - discount
}

// v3: Making the implicit explicit part 2
sealed interface CustomerV3
data class EligibleRegisteredCustomer(val id: String) : CustomerV3
data class RegisteredCustomerV3(val id: String) : CustomerV3
data class UnregisteredCustomerV3(val id: String) : CustomerV3

fun calculateTotalV3(customerV3: CustomerV3, spend: Double): Double {
    val discount = when(customerV3) {
        is EligibleRegisteredCustomer -> if (spend >= 100.0) spend * 0.1 else 0.0
        is RegisteredCustomerV3 -> 0.0
        is UnregisteredCustomerV3 -> 0.0
    }

    return spend - discount
}

fun main() {
    // fyi: real tests will be introduced later

    println("testing v1")
    calculateTotalV1(CustomerV1(id = "John", isEligible = true, isRegistered = true), 100.0) eq 90.0
    calculateTotalV1(CustomerV1(id = "Mary", isEligible = true, isRegistered = true), 99.0) eq 99.0
    calculateTotalV1(CustomerV1(id = "Richard", isEligible = false, isRegistered = true), 100.0) eq 100.0
    calculateTotalV1(CustomerV1(id = "Sarah", isEligible = false, isRegistered = false), 100.0) eq 100.0

    println("testing v2")
    calculateTotalV2(RegisteredCustomerV2(id = "John", isEligible = true), 100.0) eq 90.0
    calculateTotalV2(RegisteredCustomerV2(id = "Mary", isEligible = true), 99.0) eq 99.0
    calculateTotalV2(RegisteredCustomerV2(id = "Richard", isEligible = false), 100.0) eq 100.0
    calculateTotalV2(UnregisteredCustomerV2(id = "Sarah"), 100.0) eq 100.0

    println("testing v3")
    calculateTotalV3(EligibleRegisteredCustomer(id = "John"), 100.0) eq 90.0
    calculateTotalV3(EligibleRegisteredCustomer(id = "Mary"), 99.0) eq 99.0
    calculateTotalV3(RegisteredCustomerV3(id = "Richard"), 100.0) eq 100.0
    calculateTotalV3(UnregisteredCustomerV3(id = "Sarah"), 100.0) eq 100.0
}