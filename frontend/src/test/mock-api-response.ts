const mockApi = (data: any) => ({
  success: true,
  data,
  statusCode: 200,
  timestamp: new Date().toISOString()
});
