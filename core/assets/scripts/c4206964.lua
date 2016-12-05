-- Trap Hole

function c4206964.initialize(card)
    local effect = Duel.createEffect()
    effect:setActivationCriteria(Criteria.OnActivation)
    effect:setResponseCriteria(Response.OnNormalSummon)
    effect:setFilter(c4206964.filter)
    effect:setOperation(c4206964.operation)
    Duel.registerEffect(effect, card)
end

function c4206964.filter(card, activator)
    return card:getOwner() ~= activator and card:getAtk() >= 1000
end

function c4206964.operation(card)
    Duel.destroyCard(card)
end