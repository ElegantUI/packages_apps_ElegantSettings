/*
 * Copyright (C) 2020 TenX-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tenx.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.ServiceManager;
import android.os.UserHandle;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import com.tenx.settings.ui.DeviceThemePreferenceController;
import com.tenx.settings.ui.HeaderImagePreferenceController;
import com.tenx.settings.ui.RGBAccentPickerPreferenceController;
import com.tenx.settings.ui.RGBGradientPickerPreferenceController;
import com.tenx.settings.ui.QsTileStylePreferenceController;
import com.tenx.settings.ui.QsTileStyleTintPreferenceController;
import com.tenx.settings.ui.SwitchStylePreferenceController;
import com.tenx.settings.ui.UIStylePickerPreferenceController;

public class UserInterface extends DashboardFragment implements
        OnPreferenceChangeListener, Indexable {

    private static final String TAG = "UserInterface";

    private static final String FILE_QSPANEL_SELECT = "file_qspanel_select";
    private static final int REQUEST_PICK_IMAGE = 0;

    private Preference mQsPanelImage;

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.tenx_settings_ui;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mQsPanelImage = findPreference(FILE_QSPANEL_SELECT);
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new DeviceThemePreferenceController(context));
        controllers.add(new HeaderImagePreferenceController(context));
        controllers.add(new RGBAccentPickerPreferenceController(context));
        controllers.add(new RGBGradientPickerPreferenceController(context));
        controllers.add(new QsTileStylePreferenceController(context));
        controllers.add(new QsTileStyleTintPreferenceController(context));
        controllers.add(new SwitchStylePreferenceController(context));
        controllers.add(new UIStylePickerPreferenceController(context));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack.android"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.statusbar_height"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.seekbar_styles"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.signal_icon"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.wifi_icon"));
        return controllers;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mQsPanelImage) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            final Uri imageUri = result.getData();
            Settings.System.putString(getContentResolver(), Settings.System.QS_PANEL_CUSTOM_IMAGE, imageUri.toString());
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.TENX;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.tenx_settings_ui;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
