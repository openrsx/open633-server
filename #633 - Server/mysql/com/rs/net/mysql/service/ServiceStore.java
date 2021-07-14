package com.rs.net.mysql.service;

import com.google.common.collect.ImmutableList;
import com.rs.net.mysql.service.impl.TestService;

/**
 * Represents a serviceable MYSQL task (query) to be sent.
 * @author Dennis
 *
 */
public final class ServiceStore {

	/**
	 * An immutable list of services to be executed.
	 */
	private static ImmutableList<MYSQLService> SERVICES = ImmutableList.of(new TestService());

	/**
	 * Execute the specified service
	 * @param service
	 */
	public static void executeServiceTask(MYSQLService service) {
		SERVICES.parallelStream().filter(services -> services != service).forEach(MYSQLService::execute);
	}
}