package com.minefest.essentials.permissions;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

/**
 * COMPONENT SIGNPOST [Index: 14]
 * Purpose: Permission management system with LuckPerms integration and Forge fallback
 * Side: DEDICATED_SERVER only (permissions are server-side authority)
 * 
 * Workflow:
 * 1. [Index: 14.1] Detect if LuckPerms is available at runtime
 * 2. [Index: 14.2] Use LuckPerms API if available, otherwise fallback to Forge permissions
 * 3. [Index: 14.3] Provide unified permission checking interface for all components
 * 4. [Index: 14.4] Handle permission node hierarchy for granular control
 * 
 * Dependencies:
 * - LuckPerms API [Index: N/A] - Advanced permission system (optional)
 * - Forge Permission API [Index: N/A] - Fallback permission system
 * 
 * Related Files:
 * - MinefestConfig.java [Index: 10] - Permission settings configuration
 * - MasterClock.java [Index: 01] - Time authority permission checks
 * - AudioManager.java [Index: 05] - Audio management permission checks
 * - ServerTestBroadcaster.java [Index: 13] - Test broadcast permission checks
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class MinefestPermissions {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Permission node constants
    public static final String BASE_PERMISSION = "minefest";
    public static final String ADMIN_PERMISSION = BASE_PERMISSION + ".admin";
    public static final String AUDIO_PERMISSION = BASE_PERMISSION + ".audio";
    public static final String STREAM_PERMISSION = AUDIO_PERMISSION + ".stream";
    public static final String EVENT_PERMISSION = BASE_PERMISSION + ".event";
    public static final String TIME_PERMISSION = BASE_PERMISSION + ".time";
    public static final String TEST_PERMISSION = BASE_PERMISSION + ".test";
    
    // Specific permission nodes
    public static final String STREAM_START = STREAM_PERMISSION + ".start";
    public static final String STREAM_STOP = STREAM_PERMISSION + ".stop";
    public static final String STREAM_MANAGE = STREAM_PERMISSION + ".manage";
    public static final String EVENT_CREATE = EVENT_PERMISSION + ".create";
    public static final String EVENT_DELETE = EVENT_PERMISSION + ".delete";
    public static final String EVENT_MANAGE = EVENT_PERMISSION + ".manage";
    public static final String TIME_AUTHORITY = TIME_PERMISSION + ".authority";
    public static final String TIME_SYNC = TIME_PERMISSION + ".sync";
    public static final String TEST_BROADCAST = TEST_PERMISSION + ".broadcast";
    
    // LuckPerms integration state
    private static Boolean luckPermsAvailable = null;
    private static Object luckPermsApi = null;
    
    /**
     * [Index: 14.1] Initialize permission system and detect LuckPerms
     */
    public static void initialize() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("MinefestPermissions cannot be initialized on client side");
        }
        
        detectLuckPerms();
        LOGGER.info("MinefestPermissions initialized - LuckPerms available: {}", isLuckPermsAvailable());
    }
    
    /**
     * [Index: 14.2] Detect if LuckPerms is available
     */
    private static void detectLuckPerms() {
        try {
            // Check if LuckPerms mod/plugin is loaded
            if (ModList.get().isLoaded("luckperms")) {
                // Try to get LuckPerms API
                Class<?> luckPermsClass = Class.forName("net.luckperms.api.LuckPerms");
                java.lang.reflect.Method getApiMethod = luckPermsClass.getMethod("getApi");
                luckPermsApi = getApiMethod.invoke(null);
                luckPermsAvailable = true;
                LOGGER.info("LuckPerms detected and API loaded successfully");
            } else {
                luckPermsAvailable = false;
                LOGGER.info("LuckPerms not detected - using Forge permission fallback");
            }
        } catch (Exception e) {
            luckPermsAvailable = false;
            luckPermsApi = null;
            LOGGER.warn("Failed to load LuckPerms API, falling back to Forge permissions: {}", e.getMessage());
        }
    }
    
    /**
     * [Index: 14.3] Check if LuckPerms is available
     */
    public static boolean isLuckPermsAvailable() {
        if (luckPermsAvailable == null) {
            detectLuckPerms();
        }
        return luckPermsAvailable != null && luckPermsAvailable;
    }
    
    /**
     * [Index: 14.4] Main permission checking method - unified interface
     */
    public static boolean hasPermission(ServerPlayer player, String permission) {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Permission checks must be done server-side only");
        }
        
        if (player == null) {
            LOGGER.warn("Cannot check permissions for null player");
            return false;
        }
        
        try {
            if (isLuckPermsAvailable()) {
                return checkLuckPermsPermission(player, permission);
            } else {
                return checkForgePermission(player, permission);
            }
        } catch (Exception e) {
            LOGGER.error("Error checking permission {} for player {}: {}", 
                permission, player.getName().getString(), e.getMessage());
            return false; // Fail-safe: deny on error
        }
    }
    
    /**
     * [Index: 14.5] Check permission using LuckPerms API
     */
    private static boolean checkLuckPermsPermission(ServerPlayer player, String permission) {
        try {
            // Use reflection to call LuckPerms API methods
            Class<?> apiClass = luckPermsApi.getClass();
            java.lang.reflect.Method getUserManagerMethod = apiClass.getMethod("getUserManager");
            Object userManager = getUserManagerMethod.invoke(luckPermsApi);
            
            Class<?> userManagerClass = userManager.getClass();
            java.lang.reflect.Method getUserMethod = userManagerClass.getMethod("getUser", UUID.class);
            Object user = getUserMethod.invoke(userManager, player.getUUID());
            
            if (user != null) {
                Class<?> userClass = user.getClass();
                java.lang.reflect.Method getCachedDataMethod = userClass.getMethod("getCachedData");
                Object cachedData = getCachedDataMethod.invoke(user);
                
                Class<?> cachedDataClass = cachedData.getClass();
                java.lang.reflect.Method getPermissionDataMethod = cachedDataClass.getMethod("getPermissionData");
                Object permissionData = getPermissionDataMethod.invoke(cachedData);
                
                Class<?> permissionDataClass = permissionData.getClass();
                java.lang.reflect.Method checkPermissionMethod = permissionDataClass.getMethod("checkPermission", String.class);
                Object result = checkPermissionMethod.invoke(permissionData, permission);
                
                // LuckPerms returns Tristate (TRUE, FALSE, UNDEFINED)
                return result.toString().equals("TRUE");
            }
            
            return false;
        } catch (Exception e) {
            LOGGER.error("Error using LuckPerms API for permission {}: {}", permission, e.getMessage());
            // Fallback to Forge permissions on LuckPerms error
            return checkForgePermission(player, permission);
        }
    }
    
    /**
     * [Index: 14.6] Check permission using Forge permission system
     */
    private static boolean checkForgePermission(ServerPlayer player, String permission) {
        // Map Minefest permissions to Forge permission levels
        int requiredLevel = getPermissionLevel(permission);
        boolean hasPermission = player.hasPermissions(requiredLevel);
        
        LOGGER.debug("Forge permission check: {} requires level {}, result: {}", 
            permission, requiredLevel, hasPermission);
        
        return hasPermission;
    }
    
    /**
     * [Index: 14.7] Map permission nodes to Forge permission levels
     */
    private static int getPermissionLevel(String permission) {
        // Admin permissions require level 4 (op level 4)
        if (permission.startsWith(ADMIN_PERMISSION) || permission.equals(TIME_AUTHORITY)) {
            return 4;
        }
        
        // Management permissions require level 3 (op level 3)
        if (permission.contains(".manage") || permission.equals(EVENT_CREATE) || permission.equals(EVENT_DELETE)) {
            return 3;
        }
        
        // Stream/test permissions require level 2 (op level 2)
        if (permission.startsWith(STREAM_PERMISSION) || permission.equals(TEST_BROADCAST)) {
            return 2;
        }
        
        // Basic permissions require level 1 (op level 1)
        if (permission.startsWith(BASE_PERMISSION)) {
            return 1;
        }
        
        // Unknown permissions require level 4 (fail-safe)
        return 4;
    }
    
    /**
     * [Index: 14.8] Convenience methods for specific permission checks
     */
    
    public static boolean canManageAudio(ServerPlayer player) {
        return hasPermission(player, STREAM_MANAGE);
    }
    
    public static boolean canStartStream(ServerPlayer player) {
        return hasPermission(player, STREAM_START);
    }
    
    public static boolean canStopStream(ServerPlayer player) {
        return hasPermission(player, STREAM_STOP);
    }
    
    public static boolean canManageEvents(ServerPlayer player) {
        return hasPermission(player, EVENT_MANAGE);
    }
    
    public static boolean canCreateEvent(ServerPlayer player) {
        return hasPermission(player, EVENT_CREATE);
    }
    
    public static boolean canDeleteEvent(ServerPlayer player) {
        return hasPermission(player, EVENT_DELETE);
    }
    
    public static boolean canSetTimeAuthority(ServerPlayer player) {
        return hasPermission(player, TIME_AUTHORITY);
    }
    
    public static boolean canSyncTime(ServerPlayer player) {
        return hasPermission(player, TIME_SYNC);
    }
    
    public static boolean canTestBroadcast(ServerPlayer player) {
        return hasPermission(player, TEST_BROADCAST);
    }
    
    public static boolean isAdmin(ServerPlayer player) {
        return hasPermission(player, ADMIN_PERMISSION);
    }
    
    /**
     * [Index: 14.9] Get all permission nodes for documentation/setup
     */
    public static String[] getAllPermissionNodes() {
        return new String[] {
            BASE_PERMISSION,
            ADMIN_PERMISSION,
            AUDIO_PERMISSION,
            STREAM_PERMISSION,
            STREAM_START,
            STREAM_STOP,
            STREAM_MANAGE,
            EVENT_PERMISSION,
            EVENT_CREATE,
            EVENT_DELETE,
            EVENT_MANAGE,
            TIME_PERMISSION,
            TIME_AUTHORITY,
            TIME_SYNC,
            TEST_PERMISSION,
            TEST_BROADCAST
        };
    }
} 