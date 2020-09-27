package com.kode.test

data class DataProcessor(val sortType: SortType, val query: String = "") {
    enum class SortType(val comparator: Comparator<RecipeList>) {
        NAME_ACS(
            Comparator<RecipeList> { o1, o2 -> o1.name.compareTo(o2.name) }
        ),
        NAME_DESC(
            Comparator<RecipeList> { o1, o2 -> o2.name.compareTo(o1.name) }
        ),
        TIME_ACS(
            Comparator<RecipeList> { o1, o2 -> o1.lastUpdated.compareTo(o2.lastUpdated) }
        ),
        TIME_DESC(
            Comparator<RecipeList> { o1, o2 -> o2.lastUpdated.compareTo(o1.lastUpdated) }
        );
    }

    fun processData(data: Array<RecipeList>): Array<RecipeList> {
        val res =
            if (query.isNotEmpty())
                data.filter {
                    it.name.contains(query, true)
                            || it.description?.contains(query, true) == true
                            || it.instructions?.contains(query, true) == true
                }.toTypedArray()
            else
                data.clone()
        res.sortWith(sortType.comparator)
        return res
    }
}