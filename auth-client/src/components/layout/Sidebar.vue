<template>
  <div class="h-screen flex overflow-hidden">
    <!-- Sidebar backdrop (mobile only) -->
    <div
      v-if="isMobileOpen"
      class="fixed inset-0 bg-gray-600 bg-opacity-75 z-20 transition-opacity lg:hidden"
      @click="closeMobileSidebar"
    ></div>

    <!-- Sidebar -->
    <div
      class="fixed inset-y-0 left-0 flex flex-col z-30 transition-all duration-300 ease-in-out transform bg-gray-900"
      :class="[
        isMobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0',
        isCollapsed ? 'w-20' : 'w-72 max-w-[320px] xl:max-w-[320px] 2xl:max-w-[320px]'
      ]"
    >
      <!-- Sidebar header -->
      <div class="flex-shrink-0 bg-gray-800 shadow-md">
        <div class="flex items-center h-16">
          <div class="flex items-center justify-center" :class="isCollapsed ? 'w-full px-2' : 'px-4'">
            <div class="flex-shrink-0 bg-white p-1 rounded-md">
              <img src="/logo.png" alt="Logo" class="h-8 w-8" />
            </div>
            <h1
              v-if="!isCollapsed"
              class="ml-3 text-white font-medium text-lg transition-all duration-300 ease-in-out"
            >
              AuthenHub
            </h1>
          </div>
        </div>

        <!-- Collapse toggle button - now in its own row -->
        <div class="flex justify-center py-2 border-t border-gray-700 hidden lg:flex">
          <button
            @click="toggleCollapse"
            class="p-2 rounded-full text-white hover:bg-white hover:bg-opacity-20 focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50 transition-all duration-200"
            :title="isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                v-if="isCollapsed"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M13 5l7 7-7 7M5 5l7 7-7 7"
              />
              <path
                v-else
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M11 19l-7-7 7-7m8 14l-7-7 7-7"
              />
            </svg>
          </button>
        </div>

        <!-- Mobile close button -->
        <div class="absolute top-4 right-4 lg:hidden">
          <button
            @click="closeMobileSidebar"
            class="p-2 rounded-full text-white hover:bg-white hover:bg-opacity-20 focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50 transition-all duration-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- Sidebar content -->
      <div class="flex-1 flex flex-col overflow-y-auto bg-gray-900 shadow-inner">
        <div class="px-3 py-4">
          <div v-if="!isCollapsed" class="mb-4 px-3 py-2 text-gray-400">
            <p class="text-xs uppercase font-semibold tracking-wider">Main Navigation</p>
          </div>
          <nav class="space-y-1">
          <!-- Dashboard Menu Group -->
          <div class="space-y-1">
            <!-- Dashboard Main Link -->
            <router-link
              to="/admin"
              class="group flex items-center px-3 py-2.5 text-sm font-medium rounded-md mb-1 transition-all duration-200"
              :class="[$route.path === '/admin' ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800']"
              v-slot="{ isActive }"
            >
              <div class="flex items-center w-full">
              <div class="flex-shrink-0 w-10 flex justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  :class="[isActive ? 'text-white' : 'text-gray-400 group-hover:text-white']"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                  />
                </svg>
              </div>
              <div v-if="!isCollapsed" class="ml-3 flex-1 flex items-center justify-between min-w-0">
                <span class="truncate text-ellipsis overflow-hidden">Dashboard</span>
                <button
                  @click.prevent="toggleSubmenu('dashboard')"
                  class="ml-2 p-1 rounded-md hover:bg-gray-700 focus:outline-none"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 transition-transform duration-200"
                    :class="{ 'rotate-180': expandedMenus.dashboard }"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Tooltip for collapsed state -->
              <div
                v-if="isCollapsed"
                class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
              >
                Dashboard
              </div>
            </div>
            </router-link>

            <!-- Dashboard Submenu -->
            <div
              v-show="!isCollapsed && expandedMenus.dashboard"
              class="pl-12 space-y-1 overflow-hidden transition-all duration-500"
              :style="{
                maxHeight: !isCollapsed && expandedMenus.dashboard ? '500px' : '0px'
              }">
              <SubmenuItem :index="0" :visible="!isCollapsed && expandedMenus.dashboard">
                <router-link
                  to="/admin/overview"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/overview' }"
                >
                  Overview
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="1" :visible="!isCollapsed && expandedMenus.dashboard">
                <router-link
                  to="/admin/statistics"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/statistics' }"
                >
                  Statistics
                </router-link>
              </SubmenuItem>
            </div>
          </div>

          <!-- User Management Menu Group -->
          <div class="space-y-1">
            <!-- User Management Main Link -->
            <router-link
              to="/admin/users"
              class="group flex items-center px-3 py-2.5 text-sm font-medium rounded-md mb-1 transition-all duration-200"
              :class="[$route.path.includes('/admin/users') ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800']"
              v-slot="{ isActive }"
            >
            <div class="flex items-center w-full">
              <div class="flex-shrink-0 w-10 flex justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  :class="[isActive ? 'text-white' : 'text-gray-400 group-hover:text-white']"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
                  />
                </svg>
              </div>
              <div v-if="!isCollapsed" class="ml-3 flex-1 flex items-center justify-between min-w-0">
                <span class="truncate text-ellipsis overflow-hidden">Users</span>
                <button
                  @click.prevent="toggleSubmenu('users')"
                  class="ml-2 p-1 rounded-md hover:bg-gray-700 focus:outline-none"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 transition-transform duration-200"
                    :class="{ 'rotate-180': expandedMenus.users }"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Tooltip for collapsed state -->
              <div
                v-if="isCollapsed"
                class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
              >
                Users
              </div>
            </div>
            </router-link>

            <!-- User Management Submenu -->
            <div
              v-show="!isCollapsed && expandedMenus.users"
              class="pl-12 space-y-1 overflow-hidden transition-all duration-500"
              :style="{
                maxHeight: !isCollapsed && expandedMenus.users ? '500px' : '0px'
              }">
              <SubmenuItem :index="0" :visible="!isCollapsed && expandedMenus.users">
                <router-link
                  to="/admin/users/list"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/users/list' }"
                >
                  All Users
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="1" :visible="!isCollapsed && expandedMenus.users">
                <router-link
                  to="/admin/users/roles"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/users/roles' }"
                >
                  Roles & Permissions
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="2" :visible="!isCollapsed && expandedMenus.users">
                <router-link
                  to="/admin/users/invites"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/users/invites' }"
                >
                  Invitations
                </router-link>
              </SubmenuItem>
            </div>
          </div>

          <div v-if="!isCollapsed" class="my-1.5 border-t border-gray-800"></div>

          <!-- Analytics Menu Group -->
          <div class="space-y-1">
            <!-- Analytics Main Link -->
            <router-link
              to="/admin/analytics"
              class="group flex items-center px-3 py-2.5 text-sm font-medium rounded-md mb-1 transition-all duration-200"
              :class="[$route.path.includes('/admin/analytics') ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800']"
              v-slot="{ isActive }"
            >
            <div class="flex items-center w-full">
              <div class="flex-shrink-0 w-10 flex justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  :class="[isActive ? 'text-white' : 'text-gray-400 group-hover:text-white']"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                  />
                </svg>
              </div>
              <div v-if="!isCollapsed" class="ml-3 flex-1 flex items-center justify-between min-w-0">
                <span class="truncate text-ellipsis overflow-hidden">Analytics</span>
                <button
                  @click.prevent="toggleSubmenu('analytics')"
                  class="ml-2 p-1 rounded-md hover:bg-gray-700 focus:outline-none"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 transition-transform duration-200"
                    :class="{ 'rotate-180': expandedMenus.analytics }"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Tooltip for collapsed state -->
              <div
                v-if="isCollapsed"
                class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
              >
                Analytics
              </div>
            </div>
            </router-link>

            <!-- Analytics Submenu -->
            <div
              v-show="!isCollapsed && expandedMenus.analytics"
              class="pl-12 space-y-1 overflow-hidden transition-all duration-500"
              :style="{
                maxHeight: !isCollapsed && expandedMenus.analytics ? '500px' : '0px'
              }">
              <SubmenuItem :index="0" :visible="!isCollapsed && expandedMenus.analytics">
                <router-link
                  to="/admin/analytics/traffic"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/analytics/traffic' }"
                >
                  Traffic
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="1" :visible="!isCollapsed && expandedMenus.analytics">
                <router-link
                  to="/admin/analytics/users"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/analytics/users' }"
                >
                  User Activity
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="2" :visible="!isCollapsed && expandedMenus.analytics">
                <router-link
                  to="/admin/analytics/reports"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/analytics/reports' }"
                >
                  Reports
                </router-link>
              </SubmenuItem>
            </div>
          </div>

          <div v-if="!isCollapsed" class="my-1.5 border-t border-gray-800"></div>

          <!-- Settings Menu Group -->
          <div class="space-y-1">
            <!-- Settings Main Link -->
            <router-link
              to="/admin/settings"
              class="group flex items-center px-3 py-2.5 text-sm font-medium rounded-md mb-1 transition-all duration-200"
              :class="[$route.path.includes('/admin/settings') ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800']"
              v-slot="{ isActive }"
            >
            <div class="flex items-center w-full">
              <div class="flex-shrink-0 w-10 flex justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  :class="[isActive ? 'text-white' : 'text-gray-400 group-hover:text-white']"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
              </div>
              <div v-if="!isCollapsed" class="ml-3 flex-1 flex items-center justify-between min-w-0">
                <span class="truncate text-ellipsis overflow-hidden">Settings</span>
                <button
                  @click.prevent="toggleSubmenu('settings')"
                  class="ml-2 p-1 rounded-md hover:bg-gray-700 focus:outline-none"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 transition-transform duration-200"
                    :class="{ 'rotate-180': expandedMenus.settings }"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Tooltip for collapsed state -->
              <div
                v-if="isCollapsed"
                class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
              >
                Settings
              </div>
            </div>
            </router-link>

            <!-- Settings Submenu -->
            <div
              v-show="!isCollapsed && expandedMenus.settings"
              class="pl-12 space-y-1 overflow-hidden transition-all duration-500"
              :style="{
                maxHeight: !isCollapsed && expandedMenus.settings ? '500px' : '0px'
              }">
              <SubmenuItem :index="0" :visible="!isCollapsed && expandedMenus.settings">
                <router-link
                  to="/admin/settings/general"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/settings/general' }"
                >
                  General
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="1" :visible="!isCollapsed && expandedMenus.settings">
                <router-link
                  to="/admin/settings/security"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/settings/security' }"
                >
                  Security
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="2" :visible="!isCollapsed && expandedMenus.settings">
                <router-link
                  to="/admin/settings/appearance"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/settings/appearance' }"
                >
                  Appearance
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="3" :visible="!isCollapsed && expandedMenus.settings">
                <router-link
                  to="/admin/settings/notifications"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/settings/notifications' }"
                >
                  Notifications
                </router-link>
              </SubmenuItem>
            </div>
          </div>

          <div v-if="!isCollapsed" class="my-1.5 border-t border-gray-800"></div>

          <!-- Proxy Management Menu Group -->
          <div class="space-y-1">
            <!-- Proxy Management Main Link -->
            <router-link
              to="/admin/proxies"
              class="group flex items-center px-3 py-2.5 text-sm font-medium rounded-md mb-1 transition-all duration-200"
              :class="[$route.path.includes('/admin/proxies') ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800']"
              v-slot="{ isActive }"
            >
            <div class="flex items-center w-full">
              <div class="flex-shrink-0 w-10 flex justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  :class="[isActive ? 'text-white' : 'text-gray-400 group-hover:text-white']"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <div v-if="!isCollapsed" class="ml-3 flex-1 flex items-center justify-between min-w-0">
                <span class="truncate text-ellipsis overflow-hidden">Proxy Management</span>
                <button
                  @click.prevent="toggleSubmenu('proxies')"
                  class="ml-2 p-1 rounded-md hover:bg-gray-700 focus:outline-none"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 transition-transform duration-200"
                    :class="{ 'rotate-180': expandedMenus.proxies }"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
              </div>

              <!-- Tooltip for collapsed state -->
              <div
                v-if="isCollapsed"
                class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
              >
                Proxy Management
              </div>
            </div>
            </router-link>

            <!-- Proxy Management Submenu -->
            <div
              v-show="!isCollapsed && expandedMenus.proxies"
              class="pl-12 space-y-1 overflow-hidden transition-all duration-500"
              :style="{
                maxHeight: !isCollapsed && expandedMenus.proxies ? '500px' : '0px'
              }">
              <SubmenuItem :index="0" :visible="!isCollapsed && expandedMenus.proxies">
                <router-link
                  to="/admin/proxies"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/proxies' }"
                >
                  All Proxies
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="1" :visible="!isCollapsed && expandedMenus.proxies">
                <router-link
                  to="/admin/proxies/new"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/proxies/new' }"
                >
                  Add New Proxy
                </router-link>
              </SubmenuItem>
              <SubmenuItem :index="2" :visible="!isCollapsed && expandedMenus.proxies">
                <router-link
                  to="/admin/proxies/import"
                  class="block px-3 py-1.5 text-xs text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-all duration-200 truncate"
                  :class="{ 'text-white': $route.path === '/admin/proxies/import' }"
                >
                  Import Proxies
                </router-link>
              </SubmenuItem>
            </div>
          </div>
          </nav>
        </div>
      </div>

      <!-- Quick Actions -->
      <div v-if="!isCollapsed" class="px-3 py-4">
        <div class="mb-2 text-gray-400">
          <p class="text-xs uppercase font-semibold tracking-wider">Quick Actions</p>
        </div>
        <div class="grid grid-cols-3 gap-1">
          <button
            class="flex flex-col items-center justify-center p-2 border-gray-300 rounded-md hover:bg-gray-700 transition-colors duration-200"
            @click="$router.push('/admin/users/new')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-400 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
            <span class="text-[10px] text-gray-300 truncate">New User</span>
          </button>

          <button
            class="flex flex-col items-center justify-center p-2 border-gray-300 rounded-md hover:bg-gray-700 transition-colors duration-200"
            @click="$router.push('/admin/proxies/new')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-400 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-[10px] text-gray-300 truncate">New Proxy</span>
          </button>

          <button
            class="flex flex-col items-center justify-center p-2 border-gray-300 rounded-md hover:bg-gray-700 transition-colors duration-200"
            @click="$router.push('/admin/analytics/reports/new')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-green-400 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <span class="text-[10px] text-gray-300 truncate">New Report</span>
          </button>

          <button
            class="flex flex-col items-center justify-center p-2 border-gray-300 rounded-md hover:bg-gray-700 transition-colors duration-200"
            @click="$router.push('/admin/proxies/import')"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-green-400 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
            </svg>
            <span class="text-[10px] text-gray-300 truncate">Import Proxies</span>
          </button>
        </div>
      </div>

      <!-- Sidebar footer -->
      <div class="flex-shrink-0 border-t border-gray-800 p-4 bg-gray-900 mt-auto">
        <router-link to="/" class="flex-shrink-0 w-full group block hover:bg-gray-800 p-2 rounded-md transition-all duration-200 relative">
          <div class="flex items-center justify-center" :class="{ 'justify-center': isCollapsed, 'justify-start': !isCollapsed }">
            <div class="flex-shrink-0">
              <div v-if="authStore.user?.avatar" class="h-10 w-10 rounded-full overflow-hidden border-2 border-gray-700">
                <img :src="authStore.user.avatar" alt="User avatar" class="h-full w-full object-cover" />
              </div>
              <div v-else class="h-10 w-10 rounded-full bg-blue-600 flex items-center justify-center text-white">
                {{ authStore.user?.fullName.charAt(0) }}
              </div>
            </div>
            <div v-if="!isCollapsed" class="ml-3 truncate">
              <p class="text-sm font-medium text-white truncate">
                {{ authStore.user?.fullName }}
              </p>
              <div class="flex items-center text-xs font-medium text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
                </svg>
                Back to site
              </div>
            </div>

            <!-- Tooltip for collapsed state -->
            <div
              v-if="isCollapsed"
              class="absolute left-full ml-6 px-3 py-1 bg-gray-900 text-white text-sm rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
            >
              Back to site
            </div>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Mobile toggle button - hidden since we're using the button in AdminLayout -->
    <div class="hidden">
      <button
        @click="openMobileSidebar"
        class="p-2 rounded-md bg-gray-800 text-white shadow-lg hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-600"
        aria-label="Open menu"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M4 6h16M4 12h16M4 18h16"
          />
        </svg>
      </button>
    </div>

    <!-- No main content slot here anymore -->
    <!-- The main content is now handled by AdminLayout -->
    <div v-if="false" class="hidden">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SubmenuItem from './SubmenuItem.vue'

// Props
const props = defineProps({
  isMobileOpen: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['closeMobile'])

const authStore = useAuthStore()
const route = useRoute()

// State
const isCollapsed = ref(false)
const isMobileOpen = computed({
  get: () => props.isMobileOpen,
  set: (value) => {
    if (!value) {
      emit('closeMobile')
    }
  }
})

// Track expanded submenus
const expandedMenus = ref({
  dashboard: false,
  users: false,
  analytics: false,
  settings: false,
  proxies: false
})

// Initialize expanded menus based on current route
function initExpandedMenus() {
  if (route.path.startsWith('/admin/users')) {
    expandedMenus.value.users = true
  } else if (route.path.startsWith('/admin/analytics')) {
    expandedMenus.value.analytics = true
  } else if (route.path.startsWith('/admin/settings')) {
    expandedMenus.value.settings = true
  } else if (route.path.startsWith('/admin/proxies')) {
    expandedMenus.value.proxies = true
  } else if (route.path.startsWith('/admin') &&
    !route.path.includes('/admin/users') &&
    !route.path.includes('/admin/analytics') &&
    !route.path.includes('/admin/settings') &&
    !route.path.includes('/admin/proxies')) {
    expandedMenus.value.dashboard = true
  }
}

// Toggle submenu
function toggleSubmenu(menu) {
  expandedMenus.value[menu] = !expandedMenus.value[menu]
}

// Watch route changes to update expanded menus
watch(() => route.path, (newPath) => {
  // Only auto-expand menus if they're not already expanded
  if (newPath.startsWith('/admin/users') && !expandedMenus.value.users) {
    expandedMenus.value.users = true
  } else if (newPath.startsWith('/admin/analytics') && !expandedMenus.value.analytics) {
    expandedMenus.value.analytics = true
  } else if (newPath.startsWith('/admin/settings') && !expandedMenus.value.settings) {
    expandedMenus.value.settings = true
  } else if (newPath.startsWith('/admin/proxies') && !expandedMenus.value.proxies) {
    expandedMenus.value.proxies = true
  }
})

// Expose isCollapsed to parent components
defineExpose({
  isCollapsed
})

// Save sidebar state to localStorage
function saveState() {
  localStorage.setItem('sidebar-collapsed', isCollapsed.value.toString())
}

// Toggle sidebar collapse state
function toggleCollapse() {
  isCollapsed.value = !isCollapsed.value
  saveState()
}

// Mobile sidebar controls
function openMobileSidebar() {
  isMobileOpen.value = true
}

function closeMobileSidebar() {
  isMobileOpen.value = false
  emit('closeMobile')
}

// Close mobile sidebar on route change
watch(() => route.path, () => {
  isMobileOpen.value = false
})

// Close mobile sidebar on window resize (if desktop)
function handleResize() {
  if (window.innerWidth >= 1024) {
    isMobileOpen.value = false
  }
}

// Load saved state on mount
onMounted(() => {
  const savedState = localStorage.getItem('sidebar-collapsed')
  if (savedState !== null) {
    isCollapsed.value = savedState === 'true'
  }

  // Initialize expanded menus based on current route
  initExpandedMenus()

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>
