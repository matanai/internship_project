export const environment = {
  production: false,
  baseUrl: 'http://localhost:8080',

  // ROUTES
  login: '/signin',
  register: '/signup',
  adminUsers: '/admin/users',
  adminUserUpdate: '/admin/update',
  adminExport: '/admin/export',
  contentReport: '/content/report',
  contentMessage: '/content/message',
  contentUpload: '/content/upload',
  contentHotels: '/content/hotels',

  // MESSAGE REPORT ACTION COLORS
  actionColorAdd: '#009933',
  actionColorUpdate: '#0066ff',
  actionColorRemove: '#ff6600',

  // AUTH MESSAGES
  msgMustBeUser: 'You must be logged as USER to view this content',
  msgMustBeAdmin: 'You must be logged as ADMIN to view this content',
  msgMustBeContent: 'You must be logged as CONTENT_MANAGER to view this content',
  msgMustBeSales: 'You must be logged as SALES_MANAGER to view this content'

};



