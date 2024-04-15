package com.mrl.pixiv.setting

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.setting.components.DropDownSelector
import com.mrl.pixiv.setting.components.SettingItem
import com.mrl.pixiv.setting.viewmodel.SettingState
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.util.LocaleHelper
import com.mrl.pixiv.util.throttleClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel
import org.xmlpull.v1.XmlPullParser


@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    SettingScreen_(
        modifier = modifier,
        state = viewModel.state,
        popBack = { navHostController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun SettingScreen_(
    modifier: Modifier = Modifier,
    state: SettingState = SettingState.INITIAL,
    popBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val langs = remember { getLangs(context) }
    var currentLanguage by remember {
        mutableStateOf(
            AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "Default"
        )
    }
    Screen(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.setting))
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) {
        LazyColumn(
            modifier = modifier.padding(it),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                var expanded by remember { mutableStateOf(false) }
                // 语言
                SettingItem(
                    icon = {
                        Icon(Icons.Rounded.Translate, contentDescription = null)
                    },
                ) {
                    LaunchedEffect(currentLanguage) {
                        val locale = if (currentLanguage == "Default") {
                            LocaleListCompat.getEmptyLocaleList()
                        } else {
                            LocaleListCompat.forLanguageTags(currentLanguage)
                        }
                        AppCompatDelegate.setApplicationLocales(locale)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.app_language),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        DropDownSelector(
                            modifier = Modifier.throttleClick {
                                expanded = !expanded
                            },
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            current = currentLanguage,
                        ) {
                            langs.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = it.displayName,
                                                modifier = Modifier.padding(16.dp),
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            if (currentLanguage == it.langTag) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = null
                                                )
                                            }
                                        }

                                    }, onClick = {
                                        currentLanguage = it.langTag
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getLangs(context: Context): ImmutableList<Language> {
    val langs = mutableListOf<Language>()
    val parser = context.resources.getXml(R.xml.locales_config)
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "locale") {
            for (i in 0..<parser.attributeCount) {
                if (parser.getAttributeName(i) == "name") {
                    val langTag = parser.getAttributeValue(i)
                    val displayName = LocaleHelper.getLocalizedDisplayName(langTag)
                    if (displayName.isNotEmpty()) {
                        langs.add(
                            Language(
                                langTag,
                                displayName,
                                LocaleHelper.getDisplayName(langTag)
                            )
                        )
                    }
                }
            }
        }
        eventType = parser.next()
    }

    langs.sortBy { it.displayName }
    langs.add(0, Language("Default", context.getString(R.string.label_default), null))

    return langs.toImmutableList()
}

private data class Language(
    val langTag: String,
    val displayName: String,
    val localizedDisplayName: String?,
)