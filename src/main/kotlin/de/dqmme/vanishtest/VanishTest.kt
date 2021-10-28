package de.dqmme.vanishtest

import net.axay.kspigot.main.KSpigot
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class VanishTest : KSpigot(), CommandExecutor, Listener {
    private val hiddenPlayers: MutableList<UUID> = mutableListOf()

    override fun startup() {
        getCommand("hide")!!.setExecutor(this)
        server.pluginManager.registerEvents(this, this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§7[§eHide§7] §cYou need to run this command as a player.")
            return false
        }

        if (!sender.hasPermission("hide.hide")) {
            sender.sendMessage("§7[§eHide§7] §cYou don't have enough permissions to do that.")
            return false
        }

        if (hiddenPlayers.contains(sender.uniqueId)) {
            for (all in server.onlinePlayers) {
                all.showPlayer(this, sender)
            }
            hiddenPlayers.remove(sender.uniqueId)
            sender.sendMessage("§7[§eHide§7] §aYou are visible again.")
        } else {
            for (all in server.onlinePlayers) {
                all.hidePlayer(this, sender)
            }
            hiddenPlayers.add(sender.uniqueId)
            sender.sendMessage("§7[§eHide§7] §cYou got successfully hidden.")
        }

        return true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        for (uuid in hiddenPlayers) {
            val player = server.getPlayer(uuid) ?: return

            event.player.hidePlayer(this, player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        hiddenPlayers.remove(event.player.uniqueId)
    }
}