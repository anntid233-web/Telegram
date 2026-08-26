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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * Экран "Режим Призрака" — набор переключателей, как в AyuGram.
 * Хранение состояния — SharedPreferences ("aerogram_config").
 */
public class GhostModeSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private SharedPreferences prefs;

    private int headerRow;
    private int ghostModeRow;
    private int notReadMessagesRow;
    private int notReadStoriesRow;
    private int notSendOnlineRow;
    private int notSendTypingRow;
    private int autoOfflineRow;
    private int readOnActionRow;
    private int useDelayRow;
    private int infoRow;
    private int rowCount;

    private void updateRowsId() {
        rowCount = 0;
        headerRow = rowCount++;
        ghostModeRow = rowCount++;
        notReadMessagesRow = rowCount++;
        notReadStoriesRow = rowCount++;
        notSendOnlineRow = rowCount++;
        notSendTypingRow = rowCount++;
        autoOfflineRow = rowCount++;
        readOnActionRow = rowCount++;
        useDelayRow = rowCount++;
        infoRow = rowCount++;
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
        actionBar.setTitle("Режим Призрака");
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
            if (position == ghostModeRow) {
                togglePref("ghost_mode");
                listAdapter.notifyItemChanged(position);
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
            }
        });

        return fragmentView;
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
                    || position == readOnActionRow || position == useDelayRow;
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
                        cell.setTextAndCheck("Режим призрака", pref("ghost_mode", false), true);
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
                        cell.setTextAndCheck("Использовать отложку", pref("ghost_use_delay", false), false);
                    }
                    break;
                }
                case 2: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == infoRow) {
                        cell.setText("Автоматически ставит задержку ~12 секунд при отправке сообщений. При использовании этой функции вы не будете появляться в сети.");
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
                    || position == readOnActionRow || position == useDelayRow) {
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
