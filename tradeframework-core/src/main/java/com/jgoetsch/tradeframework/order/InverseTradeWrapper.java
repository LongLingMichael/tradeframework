/*
 * Copyright (c) 2012 Jeremy Goetsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jgoetsch.tradeframework.order;

import java.io.IOException;

import com.jgoetsch.tradeframework.Contract;
import com.jgoetsch.tradeframework.Order;
import com.jgoetsch.tradeframework.InvalidContractException;

/**
 * TradingService decorator that places all orders opposite to what would normally
 * be placed, i.e. buys become sells and sells become buys.
 * 
 * @author jgoetsch
 *
 */
public final class InverseTradeWrapper implements TradingService {

	private TradingService tradingService;

	public InverseTradeWrapper(TradingService tradingService) {
		this.tradingService = tradingService;
	}

	public void placeOrder(Contract contract, Order order) throws InvalidContractException, OrderException, IOException {
		Order inverseOrder = new Order(order);
		inverseOrder.setQuantity(-inverseOrder.getQuantity());
		tradingService.placeOrder(contract, order);
	}

	public void subscribeExecutions(ExecutionListener listener) {
		tradingService.subscribeExecutions(listener);
	}

	public void cancelExecutionSubscription(ExecutionListener listener) {
		tradingService.cancelExecutionSubscription(listener);
	}

	public void close() throws IOException {
		tradingService.close();
	}

}
