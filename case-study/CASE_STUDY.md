# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
In fulfillment operations, the biggest headache with cost tracking is handling shared or indirect costs. For instance, if a delivery truck drops off stock at three different stores from two warehouses, splitting fuel and driver wages accurately per store gets messy fast.

- Key Questions I'd Ask:
  1. How are we currently allocating shared overhead—is it a simple flat split based on square footage, or do we track Activity-Based Costing (ABC) metrics like pallet touches and labor hours?
  2. How do we account for seasonal labor spikes during peak holiday seasons?
- Considerations: Moving from simple flat allocations to Activity-Based Costing (ABC) gives us a much clearer picture of true cost-per-order and profit margins for each store location.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
To optimize fulfillment costs without degrading customer delivery times:

1. Smart Inventory Placement: Position fast-moving items in warehouses closest to the stores selling them. This drastically cuts down on last-mile freight costs and transit times.
2. Capacity & Facility Right-Sizing: When replacing an older warehouse, evaluate whether its size matches current store demand rather than paying for unused floor space.

Prioritization: I would use an Impact vs. Effort matrix. Quick wins like optimizing transport delivery routes can be implemented immediately, while warehouse replacements or IT integrations can follow on a medium-term roadmap.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
Manual data entry and end-of-month CSV exports are prone to human error and delay leadership decision-making. 

- Benefits: Real-time visibility into operational spending, faster month-end closing, and automated reconciliation between warehouse activities and general ledger accounts.
- Technical Approach: Use event-driven integration (e.g. Kafka topics or transactional outbox pattern) so warehouse actions publish financial events in near real-time. We must ensure operations are idempotent so network retries never result in duplicate financial postings.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
Accurate forecasting keeps us from over-staffing warehouses during quiet periods or under-staffing during peak promotions.

Key Factors to Include:
1. Historical order volume combined with promotional calendars (e.g., Black Friday / Cyber Monday).
2. "What-If" Scenario Analysis: Giving business users the ability to model cost impact before making operational decisions (e.g., "What happens to shipping costs if we close Warehouse A and route everything through Warehouse B?").
3. Inflationary adjustments for fuel rates, warehouse rent escalation, and hourly labor costs.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
Replacing a facility is a major capital investment (CapEx), so preserving past operational costs for that Business Unit Code is critical for ROI tracking.

- Why History Matters: Reusing the `businessUnitCode` lets leadership compare historical performance metrics (cost per unit shipped, labor efficiency) directly against the new warehouse to verify if the upgrade actually reduced operating costs.
- Cost Control Approach: We use soft archiving (`archivedAt` timestamp) rather than deleting records. This maintains historical ledger auditability while allowing the new facility to operate under a clean active slate within budget limits.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
