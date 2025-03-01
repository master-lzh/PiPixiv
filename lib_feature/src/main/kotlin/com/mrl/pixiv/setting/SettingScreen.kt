package com.mrl.pixiv.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.item.SettingItem
import com.mrl.pixiv.common.util.LocaleHelper
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToNetworkSettingScreen
import com.mrl.pixiv.common.util.throttleClick
import com.mrl.pixiv.feature.R
import com.mrl.pixiv.setting.components.DropDownSelector
import com.mrl.pixiv.setting.viewmodel.SettingState
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel
import org.xmlpull.v1.XmlPullParser


@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = koinViewModel(),
    mainNavHostController: NavHostController,
    settingNavHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    SettingScreen_(
        modifier = modifier,
        state = viewModel.state,
        popBack = { mainNavHostController.popBackStack() },
        navToNetworkScreen = settingNavHostController::navigateToNetworkSettingScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun SettingScreen_(
    modifier: Modifier = Modifier,
    state: SettingState = SettingState.INITIAL,
    popBack: () -> Unit = {},
    navToNetworkScreen: () -> Unit = {},
) {
    val context = LocalContext.current
    val langs = remember { getLangs(context) }
    var currentLanguage by remember {
        mutableStateOf(
            AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "Default"
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(RString.setting))
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
                    }
                ) {
                    LaunchedEffect(currentLanguage) {
                        val locale = if (currentLanguage == "Default") {
                            LocaleListCompat.getEmptyLocaleList()
                        } else {
                            LocaleListCompat.forLanguageTags(currentLanguage)
                        }
                        AppCompatDelegate.setApplicationLocales(locale)
                    }

                    Text(
                        text = stringResource(RString.app_language),
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

            item {
                SettingItem(
                    icon = {
                        Icon(imageVector = Icons.Rounded.NetworkWifi, contentDescription = null)
                    },
                    onClick = navToNetworkScreen
                ) {
                    Text(
                        text = stringResource(RString.network_setting),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = null
                    )
                }
            }

            item {
                SettingItem(
                    icon = {
                        Icon(imageVector = Icons.Rounded.AddLink, contentDescription = null)
                    },
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            try {
                                val intent = Intent().apply {
                                    action =
                                        Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS
                                    addCategory(Intent.CATEGORY_DEFAULT)
                                    data = Uri.parse("package:${context.packageName}")
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                }
                                context.startActivity(intent)
                            } catch (ignored: Throwable) {
                            }
                        }
                    }
                ) {
                    Column {
                        Text(
                            text = stringResource(RString.default_open),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(RString.allow_open_link),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
    langs.add(0, Language("Default", context.getString(RString.label_default), null))

    return langs.toImmutableList()
}

private data class Language(
    val langTag: String,
    val displayName: String,
    val localizedDisplayName: String?,
)