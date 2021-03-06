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
package com.jgoetsch.eventtrader.filter;

import java.util.Map;
import java.util.Set;

import com.jgoetsch.eventtrader.TradeSignal;

/**
 * Prevents processing of the given list of contracts. Setting the {@link FilterProcessor inverse}
 * flag would make this a whitelist filter allowing processing of only the given
 * symbols.
 * 
 * @author jgoetsch
 *
 */
public class SymbolBlacklistFilter extends FilterProcessor<TradeSignal> {

	private Set<String> symbols;

	@Override
	protected boolean handleProcessing(TradeSignal trade, Map<Object,Object> context) {
		return !symbols.contains(trade.getContract().getSymbol());
	}

	public void setSymbols(Set<String> symbols) {
		this.symbols = symbols;
	}

	public Set<String> getSymbols() {
		return symbols;
	}

}
