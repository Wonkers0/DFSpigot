export default {
  Leave: 
  {
    "name": "player.PlayerQuitEvent",
    "check": null,
    "specifics": {}
  },
  Join: 
  {
    "name": "player.PlayerJoinEvent",
    "check": null
  },
  RightClick: 
  {
    "name": "player.PlayerInteractEvent",
    "check": "(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND",
    "specifics": 
    {
      "block":"DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock())",
      "item":"event.getItem()"
    }
  },
  LeftClick:     
  {
    "name": "player.PlayerInteractEvent",
    "check": "(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND",
    "specifics": 
    {
      "block":"DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock())",
      "item":"event.getItem()"
    }
  },
  Sneak: 
  {
    "name": "player.PlayerToggleSneakEvent",
    "check": "event.isSneaking()",
    "specifics": {}
  },
  SwapHands: 
  {
    "name": "player.PlayerSwapHandItemsEvent",
    "check": null,
    "specifics": {}
  },
  CloseInv:
  {
    "name": "inventory.InventoryCloseEvent",
    "check": null,
    "specifics": {}
  },
  StartFly:
  {
    "name": "player.PlayerToggleFlightEvent",
    "check": "event.isFlying()",
    "specifics": {}
  },
  PickupItem:
  {
    "name": "inventory.InventoryPickupItemEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getItem()"
    }
  },
  BreakBlock:
  {
    "name": "block.BlockBreakEvent",
    "check": null,
    "specifics": 
    {
      "block": "event.getBlock().getType()"
    }
  },
  StartSprint:
  {
    "name": "player.PlayerToggleSprintEvent",
    "check": "event.isSprinting()",
    "specifics": {}
  },
  ShootBow:
  {
    "name": "entity.EntityShootBowEvent",
    "check": "event.getEntity() instanceof Player",
    "specifics": {
      "targets": {
        "default": "event.getEntity()",
        "shooter": "event.getEntity()"
      }
    }
  },
  StopFly: 
  {
    "name": "player.PlayerToggleFlightEvent",
    "check": "!event.isFlying()",
    "specifics": {}
  },
  PlayerTakeDmg:
  {
    "name": "entity.EntityDamageEvent",
    "check": "event.getEntity() instanceof Player",
    "specifics": {
      "targets": {
        "default": "(Player) event.getEntity()"
      }
    }
  },
  ProjHit:
  {
    "name": "entity.ProjectileHitEvent",
    "check": "event.getEntity().getShooter() instanceof Player",
    "specifics": {}
  },
  KillPlayer:
  {
    "name": "entity.PlayerDeathEvent",
    "check": "event.getEntity().isDead() && event.getEntity().getKiller() != null",
    "specifics": 
    {
      "targets": {
        "default": "event.getEntity().getKiller()",
        "killer": "event.getEntity().getKiller()"
      }
    }
  },
  ClickInvSlot:
  {
    "name": "inventory.InventoryClickEvent",
    "check": "event.getInventory() == event.getWhoClicked().getOpenInventory().getBottomInventory()",
    "specifics": 
    {
      "item":"event.getCurrentItem()"
    }
  },
  Respawn: 
  {
    "name": "player.PlayerRespawnEvent",
    "check": null,
    "specifics": {}
  },
  DamageEntity:
  {
    "name": "entity.EntityDamageByEntityEvent",
    "check": "event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)",
    "specifics": {}
  },
  PlayerHeal:
  {
    "name": "entity.EntityRegainHealthEvent",
    "check": "event.getEntity() instanceof Player",
    "specifics": {}
  },
  ClickPlayer:
  {
    "name": "player.PlayerInteractEntityEvent",
    "check": "event.getRightClicked() instanceof Player",
    "specifics": {}
  },
  Consume:
  {
    "name": "player.PlayerItemConsumeEvent",
    "check": null,
    "specifics": {}
  },
  Death:
  {
    "name": "player.PlayerDeathEvent",
    "check": null,
    "specifics": {}
  },
  PlaceBlock:
  {
    "name": "block.BlockPlaceEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getItemInHand()",
      "block":"event.getBlock().getType()"
    }
  },
  Walk:
  {
    "name": "player.PlayerMoveEvent",
    "check": "!DFUtilities.playerDidJump(event)",
    "specifics": {}
  },
  Dismount:
  {
    "name": "entity.EntityDismountEvent",
    "check": "event.getEntity() instanceof Player",
    "specifics": {}
  },
  CloudImbuePlayer:
  {
    "name": "entity.AreaEffectCloudApplyEvent",
    "check": "DFUtilities.cloudAffectedPlayer(event.getAffectedEntities())",
    "specifics": {}
  },
  // TODO: This event may need to change the code equivalent of the Default target from one target to multiple ^
  DropItem:
  {
    "name": "player.PlayerDropItemEvent",
    "check": null,
    "specifics": 
    {
      "item": "event.getItemDrop().getItemStack()"
    }
  },
  ChangeSlot:
  {
    "name": "player.PlayerItemHeldEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getPlayer().getInventory().getItem(event.getNewSlot())"
    }
  },
  ClickEntity:
  {
    "name": "player.PlayerInteractEntityEvent",
    "check": "!(event.getRightClicked() instanceof Player)",
    "specifics": {
      "targets": {
        "victim": "(LivingEntity) event.getRightClicked()"
      }
    }
  },
  HorseJump:
  {
    "name": "entity.HorseJumpEvent",
    "check": null,
    "specifics": {}
  },
  ShootProjectile:
  {
    "name": "entity.ProjectileLaunchEvent",
    "check": "event.getEntity().getShooter() instanceof Player",
    "specifics": {}
  },
  Unsneak:
  {
    "name": "player.PlayerToggleSneakEvent",
    "check": "!event.isSneaking()",
    "specifics": {}
  },
  Fish:
  {
    "name": "player.PlayerFishEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getPlayer().getInventory().getItemInMainHand()"
    }
  },
  BreakItem:
  {
    "name": "player.PlayerItemBreakEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getBrokenItem()"
    }
  },
  ClickMenuSlot:
  {
    "name": "inventory.InventoryClickEvent",
    "check": "event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()",
    "specifics": 
    {
      "targets": {
        "default": "event.getWhoClicked()"
      },
      "item":"event.getCurrentItem()"
    }
  },
  Riptide:
  {
    "name": "player.PlayerRiptideEvent",
    "check": null,
    "specifics": 
    {
      "item":"event.getItem()"
    }
  },
  KillMob:
  {
    "name": "entity.EntityDeathEvent",
    "check": "!(event.getEntity() instanceof Player) && (event.getEntity().getKiller() != null)",
    "specifics": {}
  },
  EntityDmgPlayer:
  {
    "name": "entity.EntityDamageByEntityEvent",
    "check": "event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)",
    "specifics": {}
  },
  StopSprint:
  {
    "name": "player.PlayerToggleSprintEvent",
    "check": "!event.isSprinting()",
    "specifics": {}
  },
  Jump:
  {
    "name": "player.PlayerMoveEvent",
    "check": "DFUtilities.playerDidJump(event)",
    "specifics": {}
  },
  ProjDmgPlayer:
  {
    "name": "entity.ProjectileHitEvent",
    "check": "event.getHitEntity() instanceof Player",
    "specifics": {}
  },
  PlayerDmgPlayer:
  {
    "name": "entity.EntityDamageByEntityEvent",
    "check": "event.getEntity() instanceof Player && event.getDamager() instanceof Player",
    "specifics": {}
  }
}
