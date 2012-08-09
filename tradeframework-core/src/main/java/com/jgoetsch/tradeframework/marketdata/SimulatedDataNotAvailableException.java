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
package com.jgoetsch.tradeframework.marketdata;

import java.io.IOException;

import com.jgoetsch.tradeframework.Contract;

public class SimulatedDataNotAvailableException extends IOException {

	private static final long serialVersionUID = 1L;
	private final Contract contract;

	public SimulatedDataNotAvailableException(Contract contract) {
		super("No simulated market data available for contract " + contract);
		this.contract = contract;
	}

	public SimulatedDataNotAvailableException(Contract contract, String s) {
		super(s);
		this.contract = contract;
	}

	public Contract getContract() {
		return contract;
	}
}
