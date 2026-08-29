package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class CustomizationSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private SharedPreferences prefs;

    private static final String[] DELETED_LABEL_OPTIONS = {"Убрать значок", "Корзинка", "Крестик", "Глазик"};

    private int headerRow;
    private int translucentDeletedRow;
    private int deletedLabelRow;
    private int navigationRow;
    private int pillStackRow;
    private int usefulHeaderRow;
    private int backgroundWorkRow;
    private int localPremiumRow;
    private int disableAdsRow;
    private int ghostStatusInListRow;
    private int rowCount;

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        translucentDeletedRow = rowCount++;
        deletedLabelRow = rowCount++;
        navigationRow = rowCount++;
        pillStackRow = rowCount++;
        usefulHeaderRow = rowCount++;
        backgroundWorkRow = rowCount++;
        localPremiumRow = rowCount++;
        disableAdsRow = rowCount++;
        ghostStatusInListRow = rowCount++;
    }

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("aerogram_config", Context.MODE_PRIVATE);
        updateRowsId();
        return super.onFragmentCreate();
    }

    private boolean pref(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    private void togglePref(String key) {
        prefs.edit().putBoolean(key, !pref(key, false)).apply();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Кастомизация");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position == translucentDeletedRow) {
                togglePref("cust_translucent_deleted");
                listAdapter.notifyItemChanged(position);
            } else if (position == deletedLabelRow) {
                showDeletedLabelDialog();
            } else if (position == backgroundWorkRow) {
                togglePref("cust_background_work");
                listAdapter.notifyItemChanged(position);
            } else if (position == localPremiumRow) {
                togglePref("cust_local_premium");
                listAdapter.notifyItemChanged(position);
            } else if (position == disableAdsRow) {
                togglePref("cust_disable_ads");
                listAdapter.notifyItemChanged(position);
            } else if (position == ghostStatusInListRow) {
                togglePref("cust_ghost_status_in_list");
                listAdapter.notifyItemChanged(position);
            }
        });

        return fragmentView;
    }

    private int deletedLabelOption() {
        return prefs.getInt("cust_deleted_label_option", 1);
    }

    private void showDeletedLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Метка удалёнок");
        int selected = deletedLabelOption();
        CharSequence[] items = new CharSequence[DELETED_LABEL_OPTIONS.length];
        for (int i = 0; i < DELETED_LABEL_OPTIONS.length; i++) {
            items[i] = (i == selected ? "✓  " : "     ") + DELETED_LABEL_OPTIONS[i];
        }
        builder.setItems(items, (dialog, which) -> {
            prefs.edit().putInt("cust_deleted_label_option", which).apply();
            listAdapter.notifyItemChanged(deletedLabelRow);
        });
        showDialog(builder.create());
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position != headerRow && position != usefulHeaderRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(mContext);
                    break;
                case 1:
                    view = new TextCheckCell(mContext);
                    break;
                default:
                    view = new TextCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == headerRow) {
                        cell.setText("Кастомизация");
                    } else if (position == usefulHeaderRow) {
                        cell.setText("Полезные функции");
                    }
                    break;
                }
                case 1: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == translucentDeletedRow) {
                        cell.setTextAndCheck("Полупрозрачные удалёнки", pref("cust_translucent_deleted", true), true);
                    } else if (position == backgroundWorkRow) {
                        cell.setTextAndCheck("Работать в фоне", pref("cust_background_work", true), true);
                    } else if (position == localPremiumRow) {
                        cell.setTextAndCheck("Локальный Telegram Premium", pref("cust_local_premium", false), true);
                    } else if (position == disableAdsRow) {
                        cell.setTextAndCheck("Отключить рекламу", pref("cust_disable_ads", true), true);
                    } else if (position == ghostStatusInListRow) {
                        cell.setTextAndCheck("Статус призрака в списке диалогов", pref("cust_ghost_status_in_list", false), false);
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == deletedLabelRow) {
                        cell.setTextAndValue("Метка удалёнок", DELETED_LABEL_OPTIONS[deletedLabelOption()], true);
                    } else if (position == navigationRow) {
                        cell.setTextAndIcon("Навигация в приложении", R.drawable.msg_settings, true);
                    } else if (position == pillStackRow) {
                        cell.setTextAndIcon("Pill Stack", R.drawable.msg_settings, false);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow || position == usefulHeaderRow) {
                return 0;
            } else if (position == translucentDeletedRow || position == backgroundWorkRow
                    || position == localPremiumRow || position == disableAdsRow
                    || position == ghostStatusInListRow) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }
}
