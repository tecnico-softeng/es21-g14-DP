describe('Item Combination Question Walk-through', () => {
    function validateQuestion(
        title,
        content,
    ) {
        cy.get('[data-cy="showQuestionDialog"]')
            .should('be.visible')
            .within($ls => {
                cy.get('.headline').should('contain', title);
                cy.get('span > p').should('contain', content);
                cy.get('[id=groupLeftH]').should('contain', 'Group Left:');
                cy.get('[id=groupRightH]').should('contain', 'Group Right:');
                cy.get('[id=groupLeft]').each(($el, index, $list) => {
                    cy.get($el).should('contain', 'Item L ' + index);
                });
                cy.get('[id=groupRight]').each(($el, index, $list) => {
                    cy.get($el).should('contain', 'Item R ' + index);

                });
            });
    }

    function validateQuestionFull(title, content) {
        cy.log('Validate question with show dialog.');

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .click();

        cy.get('button')
            .contains('close')
            .click();
    }

    before(() => {
        cy.cleanOpenEndedQuestionsByName('Cypress Question Example');
        cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
        cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    });

    after(() => {
        cy.cleanItemCombinationQuestionsByName('Cypress Item Combination Question Example');
    });

    beforeEach(() => {
        cy.demoTeacherLogin();
        cy.server();
        cy.route('GET', '/courses/*/questions').as('getQuestions');
        cy.route('GET', '/courses/*/topics').as('getTopics');
        cy.get('[data-cy="managementMenuButton"]').click();
        cy.get('[data-cy="questionsTeacherMenuButton"]').click();

        cy.wait('@getQuestions')
            .its('status')
            .should('eq', 200);

        cy.wait('@getTopics')
            .its('status')
            .should('eq', 200);
    });

    afterEach(() => {
        cy.logout();
    });

    it('Creates a new item combination question', function () {
        cy.get('button')
            .contains('New Question')
            .click();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get('[data-cy="questionTypeInput"]')
            .type('item_combination', { force: true })
            .click({ force: true });

        cy.get('[data-cy="questionTitleTextArea"]'
        ).type('Cypress Item Combination Question Example - 01', { force: true });
        cy.get(
            '[data-cy="questionQuestionTextArea"]'
        ).type('Cypress Item Combination Question Example - Content - 01', { force: true });

        cy.get(`[data-cy="questionItemsLeftInput"]`).type('Item L 0');
        cy.get('[data-cy="addItemLeftItemCombination"]').click();
        cy.get(`[data-cy="questionItemsLeftInput"]`).type('Item L 1');
        cy.get('[data-cy="addItemLeftItemCombination"]').click();

        cy.get(`[data-cy="questionItemsRightInput"]`).type('Item R 0');
        cy.get('[data-cy="addItemRightItemCombination"]').click();
        cy.get(`[data-cy="questionItemsRightInput"]`).type('Item R 1');
        cy.get('[data-cy="addItemRightItemCombination"]').click();

        cy.get('[data-cy="itemAssociationLeftItem-0"]').click();
        cy.get('[data-cy="itemAssociationRightItem-1"]').click();

        cy.get('[data-cy="itemAssociationLeftItem-0"]').click();
        cy.get('[data-cy="itemAssociationRightItem-0"]').click();


        cy.wait(1000);

        cy.route('POST', '/courses/*/questions/').as('postQuestion');

        cy.get('button')
            .contains('Save')
            .click();

        cy.wait('@postQuestion')
            .its('status')
            .should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Item Combination Question Example - 01');

        validateQuestionFull(
            'Cypress Item Combination Question Example - 01',
            'Cypress Item Combination Question Example - Content - 01'
        );
    });

    it('Can view item combination question (with button)', function() {
        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('visibility')
                    .click();
            });

        cy.get('button')
            .contains('close')
            .click();
    });

    it('Can view question (with click)', function() {
        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .click();

        cy.get('button')
            .contains('close')
            .click();
    });

    it('Can update title (with right-click)', function() {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .rightclick();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within($list => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionTitleTextArea"]')
                    .clear({ force: true })
                    .type('Cypress Question Example - 01 - Edited', { force: true });

                cy.get('button')
                    .contains('Save')
                    .click();
            });

        cy.wait('@updateQuestion')
            .its('status')
            .should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 01 - Edited');
    });

    it('Can update content (with button)', function() {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('edit')
                    .click();
            });

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within($list => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionQuestionTextArea"]')
                    .clear({ force: true })
                    .type('Cypress New Content For Question!', { force: true });

                cy.get('button')
                    .contains('Save')
                    .click();
            });

        cy.wait('@updateQuestion')
            .its('status')
            .should('eq', 200);

        validateQuestionFull(
            (title = 'Cypress Question Example - 01 - Edited'),
            (content = 'Cypress New Content For Question!')
        );
    });

    it('Can delete created  item combination question', function() {
        cy.route('DELETE', '/questions/*').as('deleteQuestion');
        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('delete')
                    .click();
            });

        cy.wait('@deleteQuestion')
            .its('status')
            .should('eq', 200);
    });


});