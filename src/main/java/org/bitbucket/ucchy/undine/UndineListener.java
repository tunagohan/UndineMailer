/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package org.bitbucket.ucchy.undine;

import org.bitbucket.ucchy.undine.sender.MailSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Undineのリスナークラス
 * @author ucchy
 */
public class UndineListener implements Listener {

    private Undine parent;

    /**
     * コンストラクタ
     * @param parent プラグイン
     */
    public UndineListener(Undine parent) {
        this.parent = parent;
    }

    /**
     * プレイヤーがインベントリを閉じた時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if ( !(event.getPlayer() instanceof Player) ) return;

        Player player = (Player)event.getPlayer();

        // インベントリを同期する
        parent.getBoxManager().syncAttachBox(player);
    }

    /**
     * プレイヤーがサーバーに参加した時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        MailSender sender = MailSender.getMailSender(player);

        // 未読のメールを表示する
        parent.getMailManager().displayUnreadOnJoin(sender);
    }

    /**
     * プレイヤーがインベントリ内をクリックした時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player)event.getWhoClicked();
        if ( !parent.getBoxManager().isOpeningAttachBox(player) ) return;

        int size = event.getInventory().getSize();
        boolean inside = (event.getRawSlot() < size);

        // 添付ボックス内へのアイテム配置を禁止する
        switch ( event.getAction() ) {
        case PLACE_ALL:
        case PLACE_ONE:
        case PLACE_SOME:
            if ( inside ) {
                event.setCancelled(true);
            }
            return;
        case MOVE_TO_OTHER_INVENTORY:
            if ( !inside ) {
                event.setCancelled(true);
            }
            return;
        case HOTBAR_SWAP:
        case HOTBAR_MOVE_AND_READD:
            event.setCancelled(true);
            return;
        default:
            return;
        }
    }

    /**
     * プレイヤーがインベントリ内をドラッグした時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Player player = (Player)event.getWhoClicked();
        if ( !parent.getBoxManager().isOpeningAttachBox(player) ) return;

        int size = event.getInventory().getSize();
        for ( int i : event.getRawSlots() ) {
            if ( i < size ) {
                event.setCancelled(true);
                return;
            }
        }
    }
}