package domain

import (
	"testing"

	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type BasketTestSuite struct {
	suite.Suite
	basket *Basket
}

func (suite *BasketTestSuite) SetupTest() {
	suite.basket = &Basket{
		ID: "basket1",
	}
}

func (suite *BasketTestSuite) TestAddItem() {
	item := BasketItem{
		ProductID: "prod1",
		Quantity:  2,
		ProductDetails: BasketItemDetails{
			Name:     "Sushi",
			Price:    decimal.NewFromInt(10),
			ImageURL: "img.jpg",
			Link:     "link",
		},
	}
	err := suite.basket.AddItem(item)
	assert.NoError(suite.T(), err)
	assert.Equal(suite.T(), 1, len(suite.basket.Items))
	assert.Equal(suite.T(), decimal.NewFromInt(20), suite.basket.TotalPrice)
}

func (suite *BasketTestSuite) TestAddItemDetails() {
	item := BasketItem{
		ProductID: "prod1",
		Quantity:  2,
	}

	err := suite.basket.AddItem(item)
	assert.NoError(suite.T(), err)

	err = suite.basket.AddItemDetails("prod1", BasketItemDetails{
		ImageURL: "img.jpg",
		Link:     "link",
		Name:     "Sushi",
		Price:    decimal.NewFromInt(10),
	})

	assert.NoError(suite.T(), err)
	assert.Equal(suite.T(), 1, len(suite.basket.Items))
	assert.Equal(suite.T(), decimal.NewFromInt(20), suite.basket.TotalPrice)
}

func (suite *BasketTestSuite) TestAddItemInvalidQuantity() {
	item := BasketItem{
		ProductID: "prod2",
		Quantity:  0,
		ProductDetails: BasketItemDetails{
			Name:     "Sushi",
			Price:    decimal.NewFromInt(10),
			ImageURL: "img.jpg",
			Link:     "link",
		},
	}
	err := suite.basket.AddItem(item)
	assert.Error(suite.T(), err)
}

func (suite *BasketTestSuite) TestRemoveItem() {
	item := BasketItem{
		ProductID: "prod1",
		Quantity:  1,
		ProductDetails: BasketItemDetails{
			Name:  "Sushi",
			Price: decimal.NewFromInt(10),
		},
	}
	_ = suite.basket.AddItem(item)
	err := suite.basket.RemoveItem("prod1")
	assert.NoError(suite.T(), err)
	assert.Equal(suite.T(), 0, len(suite.basket.Items))
}

func (suite *BasketTestSuite) TestChangeItemQuantity() {
	item := BasketItem{
		ProductID: "prod1",
		Quantity:  1,
		ProductDetails: BasketItemDetails{
			Name:  "Sushi",
			Price: decimal.NewFromInt(10),
		},
	}
	_ = suite.basket.AddItem(item)
	err := suite.basket.ChangeItemQuantity("prod1", 3)
	assert.NoError(suite.T(), err)
	assert.Equal(suite.T(), int32(3), suite.basket.Items[0].Quantity)
	assert.Equal(suite.T(), decimal.NewFromInt(30), suite.basket.TotalPrice)
}

func (suite *BasketTestSuite) TestClear() {
	item := BasketItem{
		ProductID: "prod1",
		Quantity:  1,
		ProductDetails: BasketItemDetails{
			Name:  "Sushi",
			Price: decimal.NewFromInt(10),
		},
	}
	_ = suite.basket.AddItem(item)
	suite.basket.Clear()
	assert.Equal(suite.T(), 0, len(suite.basket.Items))
	assert.True(suite.T(), suite.basket.TotalPrice.IsZero())
}

func (suite *BasketTestSuite) TestComplete() {
	suite.basket.Complete()
	assert.NotNil(suite.T(), suite.basket.CompleteDate)
}

func TestBasketTestSuite(t *testing.T) {
	suite.Run(t, new(BasketTestSuite))
}
