package com.patsy.app.pawmoji

enum class PawMojiCategory { REACTION, LOVE, DAILY, HOBBY, ADVENTURE, SEASONAL, WORK, FOOD, CELEBRATION }

data class PawMojiDefinition(
    val id: String,
    val label: String,
    val category: PawMojiCategory,
    val locked: Boolean = false,
    val assetName: String? = null
)

/**
 * Canonical PawMoji registry.
 * One registry entry = one individual PawMoji asset/keyboard item.
 * Do not use sprite sheets as the runtime PawMoji unit.
 */
object PawMojiCatalog {
    val items = listOf(
        PawMojiDefinition("happy", "Happy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("love", "Love", PawMojiCategory.LOVE, true),
        PawMojiDefinition("excited", "Excited", PawMojiCategory.REACTION, true),
        PawMojiDefinition("sad", "Sad", PawMojiCategory.REACTION, true),
        PawMojiDefinition("angry", "Angry", PawMojiCategory.REACTION, true),
        PawMojiDefinition("wink_hello", "Wink / Hello", PawMojiCategory.REACTION, true),
        PawMojiDefinition("sleepy", "Sleepy", PawMojiCategory.DAILY, true),
        PawMojiDefinition("confused_thinking", "Confused / Thinking", PawMojiCategory.REACTION, true),
        PawMojiDefinition("big_love", "Big Love", PawMojiCategory.LOVE, true),
        PawMojiDefinition("happy_waving", "Happy Waving", PawMojiCategory.REACTION, true),
        PawMojiDefinition("bath_bubbles", "Bath / Bubbles", PawMojiCategory.DAILY, true),
        PawMojiDefinition("coffee_first", "Coffee First", PawMojiCategory.FOOD, true),
        PawMojiDefinition("adventure_travel", "Adventure / Travel", PawMojiCategory.ADVENTURE, true),
        PawMojiDefinition("chill_movie", "Chill / Movie Night", PawMojiCategory.DAILY, true),
        PawMojiDefinition("play_fetch", "Play / Fetch", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("gamer", "Gamer Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("judgemental", "Judgemental Patsy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("race_car", "Race Car Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("biker", "Biker Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("bored", "Bored Patsy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("spa", "Spa Patsy", PawMojiCategory.DAILY, true),
        PawMojiDefinition("pop_star", "Pop Star Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("photography", "Photography Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("pilot", "Pilot Patsy", PawMojiCategory.ADVENTURE, true),

        // Remaining single PawMojis from the approved reference direction.
        PawMojiDefinition("laughing", "Laughing", PawMojiCategory.REACTION),
        PawMojiDefinition("heart_eyes", "Heart Eyes", PawMojiCategory.LOVE),
        PawMojiDefinition("kiss", "Kiss", PawMojiCategory.LOVE),
        PawMojiDefinition("cool", "Cool", PawMojiCategory.REACTION),
        PawMojiDefinition("hug", "Hug", PawMojiCategory.LOVE),
        PawMojiDefinition("crying", "Crying", PawMojiCategory.REACTION),
        PawMojiDefinition("anxious", "Anxious", PawMojiCategory.REACTION),
        PawMojiDefinition("sick", "Sick", PawMojiCategory.DAILY),
        PawMojiDefinition("stressed", "Stressed", PawMojiCategory.REACTION),
        PawMojiDefinition("relaxed", "Relaxed", PawMojiCategory.DAILY),
        PawMojiDefinition("cheerleader", "Cheerleader", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("rainy_day", "Rainy Day", PawMojiCategory.DAILY),
        PawMojiDefinition("cold_weather", "Cold Weather", PawMojiCategory.DAILY),
        PawMojiDefinition("beach_day", "Beach Day", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("pool_time", "Pool Time", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("camping", "Camping", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("fishing", "Fishing", PawMojiCategory.HOBBY),
        PawMojiDefinition("gardening", "Gardening", PawMojiCategory.HOBBY),
        PawMojiDefinition("office_work", "Office / Work", PawMojiCategory.WORK),
        PawMojiDefinition("detective", "Detective", PawMojiCategory.WORK),
        PawMojiDefinition("chef_cooking", "Chef / Cooking", PawMojiCategory.FOOD),
        PawMojiDefinition("foodie", "Foodie", PawMojiCategory.FOOD),
        PawMojiDefinition("munchies", "Munchies", PawMojiCategory.FOOD),
        PawMojiDefinition("ice_cream", "Ice Cream", PawMojiCategory.FOOD),
        PawMojiDefinition("pizza", "Pizza Time", PawMojiCategory.FOOD),
        PawMojiDefinition("birthday", "Birthday", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("party", "Party", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("halloween", "Halloween", PawMojiCategory.SEASONAL),
        PawMojiDefinition("christmas", "Christmas / Santa", PawMojiCategory.SEASONAL),
        PawMojiDefinition("angel", "Angel", PawMojiCategory.SEASONAL),
        PawMojiDefinition("rainbow_pride", "Rainbow", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("goodbye", "Goodbye / Bye", PawMojiCategory.REACTION),
        PawMojiDefinition("music_lover", "Music Lover", PawMojiCategory.HOBBY),
        PawMojiDefinition("tourist", "Tourist", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("classic_aviator", "Classic Aviator", PawMojiCategory.ADVENTURE)
    )

    val byId: Map<String, PawMojiDefinition> = items.associateBy { it.id }
    val approvedLocked = items.filter { it.locked }
    val awaitingIndividualAsset = items.filterNot { it.locked }
}
