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
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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

public class MessageFiltersActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private SharedPreferences prefs;

    private int headerRow;
    private int enableFiltersRow;
    private int enableCommonInChatsRow;
    private int hideFromUsersRow;
    private int commonFiltersRow;
    private int shadowBanRow;
    private int rowCount;

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        enableFiltersRow = rowCount++;
        enableCommonInChatsRow = rowCount++;
        hideFromUsersRow = rowCount++;
        commonFiltersRow = rowCount++;
        shadowBanRow = rowCount++;
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
        actionBar.setTitle("Фильтры сообщений");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 10) {
                    openSelectChatForFilter();
                } else if (id == 11) {
                    importFilters();
                } else if (id == 12) {
                    showClearFiltersConfirm();
                }
            }
        });

        final ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem otherItem = menu.addItem(0, R.drawable.ic_ab_other);
        otherItem.addSubItem(10, R.drawable.msg_contacts, "Выбрать чат");
        otherItem.addSubItem(11, R.drawable.msg_settings, "Импорт");
        otherItem.addSubItem(12, R.drawable.msg_delete, "Очистить");

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position == enableFiltersRow) {
                togglePref("filters_enabled");
                listAdapter.notifyItemChanged(position);
            } else if (position == enableCommonInChatsRow) {
                togglePref("filters_common_in_chats");
                listAdapter.notifyItemChanged(position);
            } else if (position == hideFromUsersRow) {
                togglePref("filters_hide_from_users");
                listAdapter.notifyItemChanged(position);
            } else if (position == commonFiltersRow) {
                openFilterList("Общие фильтры");
            } else if (position == shadowBanRow) {
                openFilterList("Теневой бан");
            }
        });

        return fragmentView;
    }

    private void openSelectChatForFilter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Выбрать чат");
        builder.setMessage("Выберите чат, для которого будут применяться персональные фильтры.");
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    private void importFilters() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Импорт");
        builder.setMessage("Выберите файл со списком фильтров для импорта.");
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    private void showClearFiltersConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Очистить");
        builder.setMessage("Удалить все фильтры сообщений? Это действие необратимо.");
        builder.setPositiveButton("Очистить", (dialog, which) -> {
            prefs.edit().remove("filters_enabled").remove("filters_common_in_chats").apply();
            listAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void openFilterList(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(title);
        builder.setMessage("Список фильтров пока пуст.");
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
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
            return position != headerRow;
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
                    ((HeaderCell) holder.itemView).setText("Основные");
                    break;
                }
                case 1: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == enableFiltersRow) {
                        cell.setTextAndCheck("Включить фильтры", pref("filters_enabled", false), true);
                    } else if (position == enableCommonInChatsRow) {
                        cell.setTextAndCheck("Включить общие фильтры в чатах", pref("filters_common_in_chats", false), true);
                    } else if (position == hideFromUsersRow) {
                        cell.setTextAndCheck("Скрывать от пользователей в теневом бане", pref("filters_hide_from_users", false), false);
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == commonFiltersRow) {
                        cell.setTextAndValue("Общие фильтры", "0 фильтров", true);
                    } else if (position == shadowBanRow) {
                        cell.setTextAndValue("Теневой бан", "0 фильтров", false);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) {
                return 0;
            } else if (position == enableFiltersRow || position == enableCommonInChatsRow
                    || position == hideFromUsersRow) {
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
