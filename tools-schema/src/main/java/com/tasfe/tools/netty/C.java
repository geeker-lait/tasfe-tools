package com.tasfe.tools.netty;
/**
 * @author lait.zhang@gmail.com
 * @Date 2016年8月22日-下午3:36:59
 * @Description:<br></br> 启动http服务器
 * @throws InterruptedException
 */
/**
 * 启动http服务器
 * @throws InterruptedException
 */
/*
private void runHttpServer(final EventProducer evtProducer) throws InterruptedException {
	
	// 配置TCP服务器.
	EventLoopGroup bossGroup = new NioEventLoopGroup(ServerBootOption.Parent_EventLoopGroup_ThreadNum);
	EventLoopGroup workerGroup = new NioEventLoopGroup(ServerBootOption.Child_EventLoopGroup_ThreadNum);
	try {

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 10240)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_LINGER, 0)
				//.childOption(ChannelOption.SO_TIMEOUT, ServerBootOption.SO_TIMEOUT)
				// .handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>(){
					 @Override 
				 	 protected void initChannel(SocketChannel ch) throws Exception {
						 ChannelPipeline p = ch.pipeline();
						 p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 30));<span style="color:#FF0000;">//读信道空闲60s,写信道空闲60s,读，写信道空闲30s</span>
						 p.addLast("http_server_codec", new HttpServerCodec());//<span style="color:#FF0000;">http消息转换</span>
					         p.addLast("http_server_handler",new HttpProxyServerHandler(manager,evtProducer));//<span style="color:#FF0000;">消息处理器</span>
					 }
		  
				});
		 
		// Start the tcp server.
		ChannelFuture f = b.bind(new InetSocketAddress(ip, port)).sync();//<span style="color:#FF0000;">启动http服务进程</span>
		logger.info("start http server ok");
		// Wait until the server socket is closed.
		f.channel().closeFuture().sync();
	} finally {
		// Shut down all event loops to terminate all threads.
		logger.info("Shut down all event loops to terminate all threads.");
                     bossGroup.shutdownGracefully();//关闭服务进程
		workerGroup.shutdownGracefully();//关闭服务进程
	}
}*/
