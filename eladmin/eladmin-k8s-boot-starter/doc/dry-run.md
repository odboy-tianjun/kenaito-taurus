kubectl --dry-run选项，包括三种模式：none、client和server

- none模式下，操作会实际生效
- client模式则仅在本地打印对象，不发送请求
- server模式会发送请求到服务端但不持久化资源