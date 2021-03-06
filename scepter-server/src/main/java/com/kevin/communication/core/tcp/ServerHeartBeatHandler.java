package com.kevin.communication.core.tcp;

import com.kevin.communication.core.context.BeatContext;
import com.kevin.communication.core.context.Global;
import com.kevin.communication.core.context.ServerType;
import com.kevin.communication.core.server.IMessageProcessor;
import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.enums.MessageType;
import com.kevin.message.protocol.message.HeartBeatMessage;
import com.kevin.message.protocol.message.IMessage;
import com.kevin.message.protocol.utility.FastJsonHelper;
import com.kevin.communication.core.filter.IFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 服务器心跳处理
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:05
 */
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandler.class);

    private IMessageProcessor messageProcessor;

    public ServerHeartBeatHandler(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                LOGGER.warn("Client is idle, close it.");
                messageProcessor.processReceiveDisconnect(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");

        BeatContext context = BeatContext.wrapNoSessionContext(ctx, ServerType.TCP);
        //连接进来后，查看是否在禁用IP中
        for (IFilter f : Global.getInstance().getConnectionFilterList()) {
            f.filter(context);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null) {
            Protocol p = (Protocol) msg;
            if (p.getMessageType() == MessageType.HeartBeat) {
                IMessage message = (IMessage) p.getEntity();

                LOGGER.info("read heartBeat from client {}", FastJsonHelper.toJson(message));

                //接收到心跳消息的处理逻辑
                messageProcessor.processReceiveHeartMessage(ctx, (HeartBeatMessage) message);
                return;
            }
            ctx.fireChannelRead(p);
        }
    }

}
