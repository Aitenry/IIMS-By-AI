const getters = {
  sidebar: (state: any) => state.app.sidebar,
  device: (state: any) => state.app.device,
  token: (state: any) => state.user.token,
  avatar: (state: any) => state.user.avatar,
  name: (state: any) => state.user.name,
  role: (state: any) => state.user.role,
  username: (state: any) => state.user.username,
  userId: (state: any) => state.user.userId,
  menus: (state: any) => state.user.menus,
  permissions: (state: any) => state.user.permissions,
  menusRoutes: (state: any) => state.permission.routes
}
export default getters
