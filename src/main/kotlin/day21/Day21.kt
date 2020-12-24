package day21

import java.io.File

fun main() {
    val foods = File("src/main/resources/day21_input.txt").readLines()
            .map { FoodItem.fromInputString(it) }

    val ingredients = foods.flatMap { it.ingredients }.toSet()

    val confirmedAllergens = mutableMapOf<String, String>()
    val allergenToPossibleIngredients = foods.flatMap { it.markedAllergens }.toSet()
            .map { it to ingredients }.toMap().toMutableMap() //prime map with all foods

    //intersect each possibility list to reduce the options
    foods.forEach { food ->
        food.markedAllergens.forEach { markedAllergen ->
            allergenToPossibleIngredients[markedAllergen] =
                    allergenToPossibleIngredients[markedAllergen]!! intersect food.ingredients
        }
    }

    //loop until we can confirm all allergens
    while(allergenToPossibleIngredients.isNotEmpty()) {
        //find those with only one possibility remaining
        allergenToPossibleIngredients.filter { it.value.size == 1 }.forEach { foundAllergen ->
            val foundAllergenFoodName = foundAllergen.value.first()
            val foundAllergenName = foundAllergen.key

            //add to confirmed list, remove from speculative list
            confirmedAllergens[foundAllergenName] = foundAllergenFoodName
            allergenToPossibleIngredients.remove(foundAllergenName)

            //remove the confirmed food item from the possibilities of other allergens
            allergenToPossibleIngredients.forEach { possibility ->
                allergenToPossibleIngredients[possibility.key] =
                        allergenToPossibleIngredients[possibility.key]!! - foundAllergenFoodName
            }
        }
    }

    val nonAllergenIngredients = ingredients.minus(confirmedAllergens.values)

    val appearances = nonAllergenIngredients.map { nonAllergenIngredient ->
        foods.count { it.ingredients.contains(nonAllergenIngredient) }
    }.sum()

    println(appearances)
}

data class FoodItem(val ingredients: Set<String>, val markedAllergens: Set<String>) {
    companion object {
        fun fromInputString(line: String): FoodItem {
            val markedAllergens = line
                    .substringAfter("(contains ","")
                    .substringBefore(")", "")
                    .split(", ")
                    .toSet()

            val ingredients = line
                    .substringBefore(" (contains ")
                    .split(" ")
                    .toSet()

            return FoodItem(ingredients, markedAllergens)
        }

    }
}