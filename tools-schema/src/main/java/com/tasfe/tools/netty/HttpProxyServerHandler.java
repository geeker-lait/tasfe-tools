package com.tasfe.tools.netty;

/*
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;*/

/**
 * @author lait.zhang@gmail.com
 * @Date 2016年8月22日-下午3:31:40
 * @Description:<br>http 请求事件处理器，负责分发http请求事件</br>
 * <p>
 * <p>
 * 消息分发
 * @param ctx
 * @param manager
 * @param msg 消息
 * @return
 * @throws Exception
 */
/*

public class HttpProxyServerHandler extends ChannelHandlerAdapter{
	private static final Logger logger = Logger.getLogger(HttpProxyServerHandler.class);
	private SessionContextManager manager;
	//会话管理</span>
	private final AttributeKey<Long> timestamp = AttributeKey.valueOf("timestamp");
	private final StringBuilder buf = new StringBuilder();
	public HttpProxyServerHandler(SessionContextManager manager){
		this.manager=manager;
	}
	@Override
    public void handlerAdded(final ChannelHandlerContext ctx)
            throws Exception{
    	 logger.info("["+ctx.channel().id().asLongText()+" ] is Added ");
    }
	@Override 
	//<span style="color:#FF0000;">会话关闭，失效事件</span>
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		 manager.getSession().remove(ctx.channel().id().asLongText());
		 super.channelInactive(ctx);
	}
	@Override 
	//<span style="color:#FF0000;">读消息</span><span style="color:#FF0000;">事件,业务处理的入口</span>
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
    	logger.info("HttpProxyServerHandler[channelRead] ");
    	Attribute<Long> attr_timestamp = ctx.channel().attr(timestamp);
    	attr_timestamp.set(System.currentTimeMillis());
    	//<span style="color:#FF0000;">测试信道中保存状态消息</span>，<span style="color:#FF0000;">如消息处理的开如时间</span>
        SessionContext sctx=new SessionContext();
    	sctx.setCtx(ctx);
    	sctx.setChannelId(ctx.channel().id());
    	sctx.setSessionActiveTime(""+System.currentTimeMillis());
    	manager.getSession().put(sctx.getChannelId().asLongText(), sctx);
    	//<span style="color:#FF0000;">manager.getSession() 为并发Map结构,用于会话保持</span>
    	logger.info(sctx.getChannelId().asLongText()+" req time "+System.currentTimeMillis());
    	try {
    		dispatchMeesage(ctx,manager ,msg);
        } finally {
         	 ReferenceCountUtil.release(msg);
        }

    }
	private QueryStringDecoder qdecoder =   null;
	private String uri="";//http uri
	private String url="";//http url
	private Map<String,List<String>> httpReqParams=null;//http请求参数
	*/
/**
 * 消息分发
 * @param ctx
 * @param manager
 * @param msg 消息
 * @return
 * @throws Exception
 *//*

	private String  dispatchMeesage(final ChannelHandlerContext ctx,
			final SessionContextManager manager, final Object msg) 
			throws Exception{
		String decode_message = "";
		HttpResponseUtils util = new HttpResponseUtils();
		String result_code="1";
		
		if (msg instanceof HttpRequest) {// http请求头
			HttpRequest req = (HttpRequest) msg;
			url = req.getUri();
			if (url.equals("/favicon.ico")) {
	        	ctx.close();
	            return "0";
			}
			if(!url.equals("/billing")){
				ctx.close();
				return "0";
			}
			//if (is100ContinueExpected(req)) {
	           // ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
	       // }
			qdecoder = new QueryStringDecoder(url);
			httpReqParams = qdecoder.parameters();
			uri = qdecoder.path();
			*/
/* TODO：身份认证
             * if(qdecoder.parameters().containsKey("crendial")){
			 * crendial=(String
			 * )qdecoder.parameters().get("crendial").get(0).toUpperCase(); }
			 *//*

			
		} else if (msg instanceof HttpContent) { // http请求体
			
			HttpContent httpContent = (HttpContent) msg;
			ByteBuf content = httpContent.content();
			if (content.isReadable()) {
				String chunk_message=content.toString(CharsetUtil.UTF_8);
		                 buf.append(chunk_message);
			}
			
		       if (!(msg instanceof LastHttpContent)) {//<span style="color:#FF0000;">不是最后一个chunk包块，此项非常 重要，当http分多片传输时，需要将多块内容合并</span>
	       		return "0";
	       	       }else{
	       		decode_message= buf.toString(); //
	       		logger.info(decode_message);
	       	      }
	         
			if (msg instanceof LastHttpContent) {
				//LastHttpContent trailer = (LastHttpContent) msg;
				
				String sessionId=ctx.channel().id().asLongText();
				System.out.println("请求"+decode_message);
				System.out.println("请求参数"+httpReqParams);
				System.out.println("请求地址"+uri);
				System.out.println("会话Id"+sessionId);
				//<span style="color:#FF0000;">TODO：模拟发送请求消息给后端处理,可以放入消息队列，ctx对象进入会话保持（Map对象）中，等消息队列中处理完成后，恢复会话，并完成消息应答。</span>
				
			}
			
		}
		return result_code;
	}
	private static AtomicInteger counter = new AtomicInteger(0);
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		manager.getSession().remove(ctx.channel().id().asLongText());
        cause.printStackTrace();
        ctx.close();
        
    }
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	logger.error("HttpProxyServerHandler[userEventTriggered] ");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
            	logger.error("ALL_IDLE");

            } else if (e.state() == IdleState.READER_IDLE){
            	logger.error("READER_IDLE");
    
            }else if (e.state() == IdleState.WRITER_IDLE){
            	logger.error("WRITER_IDLE");
            }
        	
        	ctx.close();
        }else if(evt instanceof ErrorEvent){
        	logger.error(((ErrorEvent) evt).getErrorCode());
        	
        	ctx.close();
        }
        
    }
}
*/

