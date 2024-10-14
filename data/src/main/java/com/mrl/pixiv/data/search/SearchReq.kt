package com.mrl.pixiv.data.search

import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.IBaseEnum
import com.mrl.pixiv.data.IBaseQueryMap

//filter=for_android
//include_translated_tag_results=true
//merge_plain_keyword_results=true
//word=原神 原神 原神 原神
//sort=popular_desc
//search_target=partial_match_for_tags
//search_ai_type=1
//@f("/v1/search/illust?filter=for_android&include_translated_tag_results=true&merge_plain_keyword_results=true")
//    s<PixivResponse> w(@i("Authorization") String str, @t("word") String str2, @t("sort") String str3, @t("search_target") String str4, @t("bookmark_num_min") Integer num, @t("bookmark_num_max") Integer num2, @t("start_date") String str5, @t("end_date") String str6);

data class SearchIllustQuery(
    val filter: Filter = Filter.ANDROID,
    val includeTranslatedTagResults: Boolean = true,
    val mergePlainKeywordResults: Boolean = true,
    val word: String,
    val sort: SearchSort = SearchSort.POPULAR_DESC,
    val searchTarget: SearchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
    val bookmarkNumMin: Int? = null,
    val bookmarkNumMax: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val searchAiType: SearchAiType = SearchAiType.HIDE_AI,
    val offset: Int = 0,
) : IBaseQueryMap


enum class SearchAiType(override val value: Int) : IBaseEnum {
    SHOW_AI(0),
    HIDE_AI(1);
}

enum class SearchSort(override val value: String) : IBaseEnum {
    DATE_DESC("date_desc"),
    DATE_ASC("date_asc"),
    POPULAR_DESC("popular_desc"),
    POPULAR_MALE_DESC("popular_male_desc"),
    POPULAR_FEMALE_DESC("popular_female_desc");
}


enum class SearchTarget(override val value: String) : IBaseEnum {
    PARTIAL_MATCH_FOR_TAGS("partial_match_for_tags"),
    EXACT_MATCH_FOR_TAGS("exact_match_for_tags"),
    TITLE_AND_CAPTION("title_and_caption"),
    TEXT("text"),
    KEYWORD("keyword");
}

data class SearchAutoCompleteQuery(
    val word: String,
    val mergePlainKeywordResults: Boolean = true,
) : IBaseQueryMap