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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class GhostModeSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private SharedPreferences prefs;

    private int selectedAccountFilter = -1;
    private boolean ghostModeExpanded = true;

    private int headerRow;
    private int ghostModeRow;
    private int notReadMessagesRow;
    private int notReadStoriesRow;
    private int notSendOnlineRow;
    private int notSendTypingRow;
    private int autoOfflineRow;
    private int readOnActionRow;
    private int infoReadOnActionRow;
    private int useDelayRow;
    private int infoDelayRow;
    private int sendSilentRow;
    private int infoSendSilentRow;
    private int suggestGhostForStoriesRow;
    private int infoSuggestGhostRow;
    private int rowCount;

    private String prefKey(String key) {
        if (selectedAccountFilter < 0) {
            return key;
        }
        return key + "_acc" + selectedAccountFilter;
    }

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        ghostModeRow = rowCount++;
        if (ghostModeExpanded) {
            notReadMessagesRow = rowCount++;
            notReadStoriesRow = rowCount++;
            notSendOnlineRow = rowCount++;
            notSendTypingRow = rowCount++;
            autoOfflineRow = rowCount++;
        } else {
            notReadMessagesRow = -1;
            notReadStoriesRow = -1;
            notSendOnlineRow = -1;
            notSendTypingRow = -1;
            autoOfflineRow = -1;
        }
        readOnActionRow = rowCount++;
        infoReadOnActionRow = rowCount++;
        useDelayRow = rowCount++;
        infoDelayRow = rowCount++;
        sendSilentRow = rowCount++;
        infoSendSilentRow = rowCount++;
        suggestGhostForStoriesRow = rowCount++;
        infoSuggestGhostRow = rowCount++;
    }

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("aerogram_config", Context.MODE_PRIVATE);
        updateRowsId();
        return super.onFragmentCreate();
    }

    private boolean pref(String key, boolean def) {
        return prefs.getBoolean(prefKey(key), def);
    }

    private void togglePref(String key) {
        prefs.edit().putBoolean(prefKey(key), !pref(key, false)).apply();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Режим Призрака");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 1000) {
                    selectedAccountFilter = -1;
                    listAdapter.notifyDataSetChanged();
                } else if (id >= 2000 && id < 2000 + UserConfig.MAX_ACCOUNT_COUNT) {
                    selectedAccountFilter = id - 2000;
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        final ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem accountItem = menu.addItem(1, R.drawable.msg_contacts);
        accountItem.setContentDescription("Аккаунт");
        accountItem.addSubItem(1000, "Общие настройки");
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                TLRPC.User user = UserConfig.getInstance(a).getCurrentUser();
                String name = user != null ? UserObject.getUserName(user) : ("Аккаунт " + (a + 1));
                accountItem.addSubItem(2000 + a, name);
            }
        }

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position == ghostModeRow) {
                ghostModeExpanded = !ghostModeExpanded;
                togglePref("ghost_mode");
                updateRowsId();
                listAdapter.notifyDataSetChanged();
            } else if (position == notReadMessagesRow) {
                togglePref("ghost_not_read_messages");
                listAdapter.notifyItemChanged(position);
            } else if (position == notReadStoriesRow) {
                togglePref("ghost_not_read_stories");
                listAdapter.notifyItemChanged(position);
            } else if (position == notSendOnlineRow) {
                togglePref("ghost_not_send_online");
                listAdapter.notifyItemChanged(position);
            } else if (position == notSendTypingRow) {
                togglePref("ghost_not_send_typing");
                listAdapter.notifyItemChanged(position);
            } else if (position == autoOfflineRow) {
                togglePref("ghost_auto_offline");
                listAdapter.notifyItemChanged(position);
            } else if (position == readOnActionRow) {
                togglePref("ghost_read_on_action");
                listAdapter.notifyItemChanged(position);
            } else if (position == useDelayRow) {
                togglePref("ghost_use_delay");
                listAdapter.notifyItemChanged(position);
            } else if (position == sendSilentRow) {
                showSendSilentDialog();
            } else if (position == suggestGhostForStoriesRow) {
                togglePref("ghost_suggest_for_stories");
                listAdapter.notifyItemChanged(position);
            }
        });

        return fragmentView;
    }

    private void showSendSilentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Отправлять без звука");
        CharSequence[] options = new CharSequence[]{"Никогда", "Всегда", "По расписанию"};
        builder.setItems(options, (dialog, which) -> {
            prefs.edit().putInt(prefKey("ghost_send_silent_mode"), which).apply();
            listAdapter.notifyItemChanged(sendSilentRow);
        });
        showDialog(builder.create());
    }

    private String sendSilentModeText() {
        int mode = prefs.getInt(prefKey("ghost_send_silent_mode"), 0);
        switch (mode) {
            case 1:
                return "Всегда";
            case 2:
                return "По расписанию";
            default:
                return "Никогда";
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ghostModeRow || position == notReadMessagesRow
                    || position == notReadStoriesRow || position == notSendOnlineRow
                    || position == notSendTypingRow || position == autoOfflineRow
                    || position == readOnActionRow || position == useDelayRow
                    || position == sendSilentRow || position == suggestGhostForStoriesRow;
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
                case 2:
                    view = new TextCell(mContext);
                    break;
                default:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    ((HeaderCell) holder.itemView).setText("Режим призрака");
                    break;
                }
                case 1: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == ghostModeRow) {
                        int checkedCount = 0;
                        if (pref("ghost_not_read_messages", true)) checkedCount++;
                        if (pref("ghost_not_read_stories", true)) checkedCount++;
                        if (pref("ghost_not_send_online", true)) checkedCount++;
                        if (pref("ghost_not_send_typing", true)) checkedCount++;
                        if (pref("ghost_auto_offline", true)) checkedCount++;
                        String value = checkedCount + "/5" + (ghostModeExpanded ? " ⌃" : " ⌄");
                        cell.setTextAndValueAndCheck("Режим призрака", value, pref("ghost_mode", false), false, true);
                    } else if (position == notReadMessagesRow) {
                        cell.setTextAndCheck("Не читать сообщения", pref("ghost_not_read_messages", true), true);
                    } else if (position == notReadStoriesRow) {
                        cell.setTextAndCheck("Не читать истории", pref("ghost_not_read_stories", true), true);
                    } else if (position == notSendOnlineRow) {
                        cell.setTextAndCheck("Не отправлять «онлайн»", pref("ghost_not_send_online", true), true);
                    } else if (position == notSendTypingRow) {
                        cell.setTextAndCheck("Не отправлять «печатает»", pref("ghost_not_send_typing", true), true);
                    } else if (position == autoOfflineRow) {
                        cell.setTextAndCheck("Автоматический «офлайн»", pref("ghost_auto_offline", true), true);
                    } else if (position == readOnActionRow) {
                        cell.setTextAndCheck("Читать при действиях", pref("ghost_read_on_action", true), true);
                    } else if (position == useDelayRow) {
                        cell.setTextAndCheck("Использовать отложку", pref("ghost_use_delay", false), true);
                    } else if (position == suggestGhostForStoriesRow) {
                        cell.setTextAndCheck("Предлагать призрака для сторис", pref("ghost_suggest_for_stories", true), false);
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == sendSilentRow) {
                        cell.setTextAndValue("Отправлять без звука", sendSilentModeText(), false);
                    }
                    break;
                }
                case 3: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == infoReadOnActionRow) {
                        cell.setText("Автоматически читает сообщение при отправке нового или при реакции на сообщение.");
                    } else if (position == infoDelayRow) {
                        cell.setText("Автоматически ставит задержку в ~12 секунд (дольше для сообщений с вложениями) при отправке сообщений. При использовании этой функции вы не будете появляться в сети. Не рекомендуется использовать на слабом интернете.");
                    } else if (position == infoSendSilentRow) {
                        cell.setText("Отправляет сообщения по умолчанию без звука.");
                    } else if (position == infoSuggestGhostRow) {
                        cell.setText("Показывает предупреждение перед открытием сторис, предлагая включить режим призрака.");
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) {
                return 0;
            } else if (position == ghostModeRow || position == notReadMessagesRow
                    || position == notReadStoriesRow || position == notSendOnlineRow
                    || position == notSendTypingRow || position == autoOfflineRow
                    || position == readOnActionRow || position == useDelayRow
                    || position == suggestGhostForStoriesRow) {
                return 1;
            } else if (position == sendSilentRow) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }
}
