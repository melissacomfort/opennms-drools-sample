#WAT-1661 - only take effect if node['onguard']['video']['enabled'] is true
directory "#{node['opennms']['conf']['home']}/etc/drools-engine.d/video_NOC_poller_services" do
	owner 'root'
	group 'root'
	mode 00755
end

template "#{node['opennms']['conf']['home']}/etc/drools-engine.d/video_NOC_poller_services/drools-engine.xml" do
	source 'video-Poll-Service-drool-engine.xml.erb'
	owner 'root'
	group 'root'
	mode 00644
	variables(
		nocHoldDownTimer: node['onguard']['correlations']['poller_service']['hold_down_timer'],
		)
	notifies :restart, 'service[opennms]'
end

cookbook_file "#{node['opennms']['conf']['home']}/etc/drools-engine.d/video_NOC_poller_services/NodeDownRules.drl" do
	source 'NodeDownRules.drl'
	owner 'root'
	group 'root'
	mode 00644
	notifies :restart, 'service[opennms]'
end

cookbook_file "#{node['opennms']['conf']['home']}/etc/drools-engine.d/video_NOC_poller_services/InterfaceDownRules.drl" do
	source 'InterfaceDownRules.drl'
	owner 'root'
	group 'root'
	mode 00644
	notifies :restart, 'service[opennms]'
end

cookbook_file "#{node['opennms']['conf']['home']}/etc/drools-engine.d/video_NOC_poller_services/NodeLostServiceRules.drl" do
	source 'NodeLostServiceRules.drl'
	owner 'root'
	group 'root'
	mode 00644
	notifies :restart, 'service[opennms]'
end

opennms_eventconf 'noc-correlations.events.xml' do
	position 'top'
	notifies :run, 'opennms_send_event[restart_Eventd]'
end

node_down_event = 'uei.opennms.org/nodes/nodeDown'
interface_down_event = 'uei.opennms.org/nodes/interfaceDown'
node_lost_service_event = 'uei.opennms.org/nodes/interfaceDown'
file_name = 'events/opennms.events.xml'

opennms_event node_down_event do
	file file_name
	position 'top'
	mask [{'mename' => 'uei', 'mevalue' => [node_down_event]}]
	event_label 'Node Down Event';
	descr 'outage identified on node %nodelabel% A new Outage record has been created and service level availability calculations will be impacted until this outage is resolved.'
	logmsg ' outage identified on node %nodelabel%'
	severity 'Minor'
	alarm_data false
	notifies :run, 'opennms_send_event[restart_Eventd]'
end

opennms_event interface_down_event do
	file file_name
	position 'top'
	mask [{'mename' => 'uei', 'mevalue' => [interface_down_event]}]
	event_label 'Interface Down Event';
	descr 'outage identified on interface %interface% on node %nodelabel% A new Outage record has been created and service level availability calculations will be impacted until this outage is resolved.'
	logmsg ' outage identified on interface %interface% on node %nodelabel%'
	severity 'Minor'
	alarm_data false
	notifies :run, 'opennms_send_event[restart_Eventd]'
end
opennms_event node_lost_service_event do
	file file_name
	position 'top'
	mask [{'mename' => 'uei', 'mevalue' => [node_lost_service_event]}]
	event_label 'Node Lost Service Event';
	descr 'A Service %service% has been lost identified on interface %interface% on node %nodelabel% A new Outage record has been created and service level availability calculations will be impacted until this outage is resolved.'
	logmsg 'A Service %service% has been lost identified on interface %interface% on node %nodelabel%'
	severity 'Minor'
	alarm_data false
	notifies :run, 'opennms_send_event[restart_Eventd]'
end