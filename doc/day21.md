# Day 21: Allergen Assessment
[view source](/src/main/kotlin/day21/Day21.kt)
## Part 1
### Problem
We are given a list of foods with ingredient names and a list of
marked allergens. There are a few rule regulating this:
1. Not all allergens may be marked in any given food.
2. Each allergen occurs in only one ingredient.
3. Marked allergens occur in one of the listed ingredients.
Find the non-allergenic ingredients and how often they occur in the
lists.
### Solution
I start by defining a class for each food, and a method to parse 
input into foods.
```
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
```
Using this, I can start defining a few things.
1. All foods from the input.
2. The unique ingredients as a set.
3. The allergens that can be confirmed to a specific ingredient. (empty for now)
4. The possible ingredients that could be the source of each allergen. (starting with all ingredients as
a possibility)
```
    val foods = File("src/main/resources/day21_input.txt").readLines()
            .map { FoodItem.fromInputString(it) }
    val ingredients = foods.flatMap { it.ingredients }.toSet()
    val confirmedAllergens = mutableMapOf<String, String>()
    val allergenToPossibleIngredients = foods.flatMap { it.markedAllergens }.toSet()
            .map { it to ingredients }.toMap().toMutableMap()
```
The next step is use `intersect` to reduce the possibilities based on known info. For each food
that contains an allergen, that allergen's possibilities can be reduced to what is shared between
the known possibilities and that food's ingredient list.
```
    foods.forEach { food ->
        food.markedAllergens.forEach { markedAllergen ->
            allergenToPossibleIngredients[markedAllergen] =
                    allergenToPossibleIngredients[markedAllergen]!! intersect food.ingredients
        }
    }
```
This leaves us with a simplified set of possibilities, and because our puzzle input is nice
one of them will be left with only one possibility. We can now move this item from the list
of possibilities to the confirmed list.

With it removed from possibilities, we further refine the choices and reveal another confirmation.
So we loop this procedure until everything is confirmed.
```
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
```
From here it's a simple removal of allergens from the ingredient list, then a quick
count and sum of appearances.
```
    val nonAllergenIngredients = ingredients.minus(confirmedAllergens.values)

    val appearances = nonAllergenIngredients.map { nonAllergenIngredient ->
        foods.count { it.ingredients.contains(nonAllergenIngredient) }
    }.sum()

    println(appearances)
```

## Part 1
### Problem
Get a list of allergen foods sorted alphabetically by allergen.
### Solution
Since we already have a list of allergens, we just sort and print.
```
confirmedAllergens.toSortedMap().values.joinToString(separator=",").apply { println(this) }
```